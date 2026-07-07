# Manual de Usuario — Dashboard RRHH

API REST para la gestión de RRHH de un depósito: empleados, fichaje de asistencia, horarios,
productividad, objetivos y reportes. Este manual describe qué puede hacer cada rol y cómo usar
cada funcionalidad.

> No hay frontend todavía: todo se usa contra la API REST (por ejemplo con `curl`, Postman o
> Insomnia). Los ejemplos de este manual usan `curl`.

## 1. Roles del sistema

| Rol | Qué puede hacer |
|---|---|
| **EMPLEADO** | Fichar su propia entrada/salida, cargar su propia productividad hora a hora, ver sus propios objetivos y su progreso. |
| **ADMINISTRADOR** | Todo lo de gestión: asignar horarios, cargar/corregir productividad de cualquiera, crear objetivos, ver productividad y asistencia de todos, exportar reportes. **No puede** dar de alta empleados. |
| **SUPERADMIN** | Todo lo de ADMINISTRADOR, más dar de alta empleados (es el único rol que puede). |
| **SUPERVISOR** | Ver la productividad y la asistencia de todos los empleados (general e individual) y exportar reportes. No gestiona horarios ni objetivos ni da de alta empleados. Como también es un empleado de la empresa, puede fichar su propia asistencia y cargar su propia productividad. |

Todos los roles, salvo aclaración, fichan su propia asistencia y cargan su propia productividad
de la misma forma que un EMPLEADO — la diferencia entre roles está en qué pueden ver o
administrar de **otros** empleados.

## 2. Iniciar sesión

Todo el sistema (salvo los endpoints marcados como "abiertos" en el catálogo del final) requiere
un token JWT obtenido al loguearse con email y contraseña.

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"superadmin@example.com","password":"changeme"}'
```

Devuelve `{"token": "..."}`. Ese token se manda en cada pedido protegido como header:

```
Authorization: Bearer <token>
```

El token expira (por defecto a los 60 minutos, configurable con `app.jwt.expiration-minutes`) y
contiene el id del empleado y su rol — el sistema los usa para saber quién sos y qué podés hacer,
sin que tengas que mandarlos vos en el cuerpo del pedido.

### El primer SuperAdmin

Como solo el SUPERADMIN puede dar de alta empleados, el sistema crea uno automáticamente la
primera vez que arranca (si todavía no existe ninguno), con estas credenciales por defecto en
desarrollo:

- Email: `superadmin@example.com` (configurable con la variable de entorno `SUPERADMIN_EMAIL`)
- Contraseña: `changeme` (configurable con `SUPERADMIN_PASSWORD`)

**Cambiar esta contraseña en cualquier ambiente que no sea desarrollo local.**

## 3. Empleados

### Dar de alta un empleado (solo SUPERADMIN)

```bash
curl -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token-de-superadmin>" \
  -d '{
    "nombre": "Juan",
    "apellido": "Lopez",
    "dni": 30111222,
    "sector": "Deposito",
    "puesto": "Operario",
    "email": "juan@example.com",
    "passwordHash": "claveTemporal123",
    "role": "EMPLEADO"
  }'
```

El campo `passwordHash` recibe la contraseña **en texto plano** — el sistema la encripta
automáticamente antes de guardarla; nunca queda ni se devuelve en texto plano por la API.

Si no se especifica `role`, el empleado queda como `EMPLEADO` por defecto.

### Buscar empleados

- Por DNI: `GET /api/empleados/buscar/dni/{dni}`
- Por nombre (búsqueda parcial, sin importar mayúsculas): `GET /api/empleados/buscar/nombre/{nombre}`
- Por apellido (igual criterio): `GET /api/empleados/buscar/apellido/{apellido}`
- Por sector: `GET /api/empleados/sector/{sector}`
- Por puesto: `GET /api/empleados/puesto/{puesto}`

Todas devuelven `204 No Content` si no encuentran nada.

### Listar, actualizar y eliminar

- `GET /api/empleados` — listado completo.
- `GET /api/empleados/{id}` — uno por id.
- `PUT /api/empleados/{id}` — actualiza nombre/apellido/sector/puesto.
- `DELETE /api/empleados/{id}`.
- `GET /api/empleados/total`, `/contar/sector/{sector}`, `/contar/puesto/{puesto}` — contadores.

## 4. Asistencia (fichaje)

Cada empleado ficha su propia entrada y salida — la hora la pone el servidor, nunca el cliente,
así que no se puede "cargar" una hora distinta a la real.

```bash
# Entrada
curl -X POST http://localhost:8080/api/attendance/2/clock-in \
  -H "Authorization: Bearer <token-del-empleado-id-2>"

# Salida
curl -X POST http://localhost:8080/api/attendance/2/clock-out \
  -H "Authorization: Bearer <token-del-empleado-id-2>"
```

Reglas:
- No se puede fichar una segunda entrada sin haber fichado la salida anterior (409).
- No se puede fichar en nombre de otro empleado — el `{employeeId}` de la URL tiene que ser el
  mismo que el del token (403 si no coincide).

Ver todos los fichajes o los de un empleado puntual (ADMINISTRADOR/SUPERADMIN/SUPERVISOR):

- `GET /api/attendance` — todos.
- `GET /api/attendance/empleado/{employeeId}` — de un empleado.

## 5. Horarios (ADMINISTRADOR/SUPERADMIN)

Asignar, actualizar y quitar el horario de trabajo esperado de un empleado (días de la semana +
hora de entrada/salida). Un empleado tiene, a lo sumo, un horario vigente a la vez.

```bash
curl -X POST http://localhost:8080/api/work-schedules \
  -H "Content-Type: application/json" -H "Authorization: Bearer <token-admin>" \
  -d '{
    "employeeId": 2,
    "workDays": ["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"],
    "startTime": "08:00:00",
    "endTime": "17:00:00"
  }'
```

- `PUT /api/work-schedules/{id}` — cambia días/horario.
- `DELETE /api/work-schedules/{id}` — quita el horario asignado.
- `GET /api/work-schedules`, `GET /api/work-schedules/{id}`, `GET /api/work-schedules/employee/{employeeId}`.

## 6. Productividad

Hay dos formas de cargar productividad, para dos casos distintos:

### 6.1. Autocarga horaria (cualquier empleado logueado)

Cada empleado carga, hora a hora, cuántos pedidos (y opcionalmente bultos) preparó. El empleado y
el momento de la carga los pone el servidor — no se puede cargar en nombre de otro ni con una
hora distinta a la real. Como mucho una carga por hora calendario (la segunda carga dentro de la
misma hora da 409).

```bash
curl -X POST http://localhost:8080/api/productividad/registro \
  -H "Content-Type: application/json" -H "Authorization: Bearer <token-del-empleado>" \
  -d '{"pedidosPreparados": 10, "bultosPreparados": 4}'
```

### 6.2. Carga administrativa (ADMINISTRADOR/SUPERADMIN)

Para cargar o corregir la productividad diaria de cualquier empleado (por ejemplo, para volcar
datos históricos):

```bash
curl -X POST http://localhost:8080/api/productividad \
  -H "Content-Type: application/json" -H "Authorization: Bearer <token-admin>" \
  -d '{"empleado": 2, "fecha": "2026-07-06", "pedidosEncargados": 50, "pedidosPreparados": 40, "bultosPreparados": 15}'
```

### 6.3. Consultas y KPIs (ADMINISTRADOR/SUPERADMIN/SUPERVISOR)

- `GET /api/productividad` — todo.
- `GET /api/productividad/empleado/{empleadoId}` — de un empleado.
- `GET /api/productividad/empleado/nombre/{nombre}` — por nombre.
- `GET /api/productividad/fecha?fecha=2026-07-06` — por fecha.
- `GET /api/productividad/empleado/{empleadoId}/kpi` — totales de ese empleado (pedidos, bultos, pendientes).
- `GET /api/productividad/kpi/global` — totales por empleado, todos juntos.

## 7. Objetivos

El ADMINISTRADOR o SUPERADMIN le asigna a un empleado una meta semanal (cantidad de pedidos, o de
dinero). La meta diaria se calcula dividiendo la semanal en 6 días laborales fijos.

```bash
curl -X POST http://localhost:8080/api/objetivos \
  -H "Content-Type: application/json" -H "Authorization: Bearer <token-admin>" \
  -d '{"empleadoId": 2, "tipo": "PEDIDOS", "valorSemanal": 120}'
```

- `tipo` puede ser `PEDIDOS` o `DINERO`.
- `semanaInicio` es opcional — si no se manda, se toma el lunes de la semana actual.
- `PUT /api/objetivos/{id}` — cambia el valor semanal.
- `DELETE /api/objetivos/{id}`.

### Ver el progreso

`GET /api/objetivos/{id}/progreso` — el propio empleado dueño del objetivo puede verlo (o
cualquier rol de gestión). Para `tipo=PEDIDOS`, cruza automáticamente lo cargado en Productividad
(sección 6.1) contra la meta:

```json
{
  "objetivo": { "id": 1, "empleadoId": 2, "tipo": "PEDIDOS", "valorSemanal": 120.0, "valorDiario": 20.0, "semanaInicio": "2026-07-06" },
  "cargadoHoy": 10.0,
  "pendienteHoy": 10.0,
  "cargadoSemana": 10.0,
  "pendienteSemana": 110.0
}
```

**Objetivos de tipo `DINERO`** se guardan igual, pero `cargadoHoy`/`cargadoSemana` quedan en
`null` — el sistema todavía no tiene ningún módulo que registre ventas o dinero para cruzar
automáticamente ese progreso (queda como una meta informativa hasta que exista esa fuente de
datos).

- `GET /api/objetivos/{id}` — el objetivo sin el cálculo de progreso.
- `GET /api/objetivos/empleado/{empleadoId}` — todos los objetivos de un empleado.

## 8. Reportes (ADMINISTRADOR/SUPERADMIN/SUPERVISOR)

Exportan la productividad o la asistencia completa, en Excel o CSV.

```bash
curl "http://localhost:8080/api/reportes/productividad?formato=excel" \
  -H "Authorization: Bearer <token-supervisor>" -o productividad.xlsx

curl "http://localhost:8080/api/reportes/asistencia?formato=csv" \
  -H "Authorization: Bearer <token-supervisor>" -o asistencia.csv
```

`formato` acepta `excel` (por defecto) o `csv`.

## 9. Migración desde Excel

`POST /api/excel/import` (sin restricción de rol hoy) — sube un `.xlsx`/`.xls` con hojas
`Empleados`/`Productividad_Diaria`/`Asistencia_Diaria` y carga/actualiza esos datos en la base,
matcheando empleados por nombre + apellido.

```bash
curl -X POST http://localhost:8080/api/excel/import \
  -F "file=@Dataset_RRHH_Deposito.xlsx"
```

## 10. Catálogo completo de endpoints

| Método | Ruta | Roles permitidos |
|---|---|---|
| POST | /api/auth/login | Público |
| GET | /api/health, /api/info, /api | Público |
| GET | /api/empleados | Abierto (sin restricción de rol) |
| GET | /api/empleados/{id} | Abierto |
| POST | /api/empleados | **SUPERADMIN** |
| PUT | /api/empleados/{id} | Abierto |
| DELETE | /api/empleados/{id} | Abierto |
| GET | /api/empleados/sector/{sector}, /puesto/{puesto} | Abierto |
| GET | /api/empleados/contar/sector/{sector}, /contar/puesto/{puesto}, /total | Abierto |
| GET | /api/empleados/buscar/dni/{dni}, /buscar/nombre/{nombre}, /buscar/apellido/{apellido} | Abierto |
| POST | /api/attendance/{employeeId}/clock-in, /clock-out | EMPLEADO, ADMINISTRADOR, SUPERADMIN, SUPERVISOR (solo el propio id) |
| GET | /api/attendance | ADMINISTRADOR, SUPERADMIN, SUPERVISOR |
| GET | /api/attendance/empleado/{employeeId} | ADMINISTRADOR, SUPERADMIN, SUPERVISOR |
| POST/PUT/DELETE | /api/work-schedules... | ADMINISTRADOR, SUPERADMIN |
| GET | /api/work-schedules... | ADMINISTRADOR, SUPERADMIN |
| POST | /api/productividad | ADMINISTRADOR, SUPERADMIN |
| GET | /api/productividad... (listado, KPIs) | ADMINISTRADOR, SUPERADMIN, SUPERVISOR |
| POST | /api/productividad/registro | EMPLEADO, ADMINISTRADOR, SUPERADMIN, SUPERVISOR (propio) |
| POST/PUT/DELETE | /api/objetivos | ADMINISTRADOR, SUPERADMIN |
| GET | /api/objetivos/{id}, /{id}/progreso, /empleado/{empleadoId} | Cualquier rol logueado, pero EMPLEADO solo ve lo propio |
| GET | /api/reportes/productividad, /asistencia | ADMINISTRADOR, SUPERADMIN, SUPERVISOR |
| POST | /api/excel/import | Abierto |

**Nota:** "Abierto" significa que no exige rol específico (alcanza con que el request llegue), no
que sea completamente público a nivel de red — hoy el proyecto tiene esos endpoints así porque
así estaban antes de sumar roles; no fue parte de lo pedido en esta ronda de cambios.
