# Manual de Usuario — Dashboard RRHH

API REST para la gestión de RRHH de un depósito: empleados, fichaje de asistencia (con
verificación facial), horarios, productividad y objetivos. Este manual describe qué puede hacer
cada rol y cómo usar cada funcionalidad.

> No hay frontend todavía: todo se usa contra la API REST (por ejemplo con `curl`, Postman o
> Insomnia). Los ejemplos de este manual usan `curl`.

> La verificación facial del fichaje de Entrada (sección 4.1) depende de un microservicio aparte
> (`face-recognition-service/`, Python) — sin levantarlo, todas las entradas quedan
> `PENDIENTE_REVISION` (nunca bloquea el fichaje). Ver su `README.md` para instalarlo y correrlo.

## 1. Roles del sistema

| Rol | Qué puede hacer |
|---|---|
| **EMPLEADO** | Fichar su propia entrada/salida, cargar su propia productividad diaria (mientras tenga una Entrada fichada), ver sus propios objetivos y sus propios KPIs/promedios de productividad. |
| **ADMINISTRADOR** | Asignar horarios, crear/actualizar/eliminar objetivos, ver productividad, KPIs y asistencia de todos. **No puede** dar de alta empleados ni cargar productividad en nombre de otro (eso solo lo hace cada empleado por sí mismo, sección 6). |
| **SUPERADMIN** | Dar de alta empleados (es el único rol que puede) y gestionar objetivos como ADMINISTRADOR. No tiene acceso a las consultas de productividad/KPIs de otros empleados (ver catálogo, sección 9). |
| **SUPERVISOR** | Ver la productividad, KPIs y asistencia de todos los empleados (general e individual). No gestiona horarios ni objetivos ni da de alta empleados. Como también es un empleado de la empresa, puede fichar su propia asistencia y cargar su propia productividad. |

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

El alta es `multipart/form-data` (no JSON): una parte `empleado` con los datos (en JSON) y una
parte `foto` opcional — la foto de referencia para la verificación facial del fichaje de Entrada
(sección 4). Si no se enrola una foto acá, ese empleado no tiene manera de fichar con
verificación automática; sus entradas van a quedar siempre pendientes de revisión humana.

```bash
curl -X POST http://localhost:8080/api/empleados \
  -H "Authorization: Bearer <token-de-superadmin>" \
  -F 'empleado={"nombre":"Juan","apellido":"Lopez","dni":30111222,"sector":"Deposito","puesto":"Operario","email":"juan@example.com","passwordHash":"claveTemporal123","role":"EMPLEADO"};type=application/json' \
  -F "foto=@juan.jpg"
```

El campo `passwordHash` recibe la contraseña **en texto plano** — el sistema la encripta
automáticamente antes de guardarla; nunca queda ni se devuelve en texto plano por la API.

Si no se especifica `role`, el empleado queda como `EMPLEADO` por defecto.

La foto de referencia se puede consultar (no viaja en el JSON del empleado) con
`GET /api/empleados/{id}/foto` (ADMINISTRADOR/SUPERADMIN/SUPERVISOR).

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
# Entrada (multipart, foto opcional pero recomendada — ver mas abajo)
curl -X POST http://localhost:8080/api/attendance/2/clock-in \
  -H "Authorization: Bearer <token-del-empleado-id-2>" \
  -F "foto=@captura.jpg"

# Salida (sin foto, sin cambios)
curl -X POST http://localhost:8080/api/attendance/2/clock-out \
  -H "Authorization: Bearer <token-del-empleado-id-2>"
```

Reglas:
- No se puede fichar una segunda entrada sin haber fichado la salida anterior (409).
- No se puede fichar en nombre de otro empleado — el `{employeeId}` de la URL tiene que ser el
  mismo que el del token (403 si no coincide).

### 4.1. Verificación facial en la Entrada

Al fichar Entrada con una foto (parte `foto`), el sistema la compara contra la foto de referencia
del empleado (cargada al darlo de alta, sección 3) usando un microservicio propio
(`face-recognition-service/`, ver su `README.md` para levantarlo). **La entrada se registra
siempre**, tenga o no foto, coincida o no la cara — lo único que cambia es el
`estadoVerificacion` del fichaje:

| Estado | Cuándo pasa |
|---|---|
| `VERIFICADO_AUTOMATICO` | Se detectó el rostro en ambas fotos y la similitud alcanzó el umbral (`app.reconocimiento-facial.umbral-auto`, 0.75 por defecto). |
| `PENDIENTE_REVISION` | Similitud insuficiente, no se detectó el rostro en alguna foto, el empleado no tiene foto de referencia, o el microservicio no respondió. |
| `VERIFICADO_MANUAL` / `RECHAZADO` | Un humano ya revisó un fichaje que estaba `PENDIENTE_REVISION` (ver más abajo). |

Si no se manda `foto` en el clock-in, el fichaje queda sin `estadoVerificacion` (no aplica).

### 4.2. Revisar fichajes pendientes (ADMINISTRADOR/SUPERADMIN/SUPERVISOR)

```bash
# Listar los pendientes
curl http://localhost:8080/api/attendance/pendientes -H "Authorization: Bearer <token-admin>"

# Ver la foto capturada en un fichaje puntual
curl http://localhost:8080/api/attendance/7/foto -H "Authorization: Bearer <token-admin>" -o foto7.jpg

# Aprobar o rechazar (compara foto7.jpg contra la de referencia del empleado, a simple vista)
curl -X POST http://localhost:8080/api/attendance/7/revisar \
  -H "Content-Type: application/json" -H "Authorization: Bearer <token-admin>" \
  -d '{"aprobado": true}'
```

Rechazar **no borra** el fichaje — queda marcado `RECHAZADO`, para no perder el registro de
auditoría (la entrada existió a esa hora; un humano determinó después que la foto no
correspondía). Solo se puede revisar un fichaje que esté `PENDIENTE_REVISION` (409 si no).

Ver también: `GET /api/attendance`, `GET /api/attendance/empleado/{employeeId}` — listados
generales, ya existentes.

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

Cada empleado carga su propia productividad diaria — el empleado lo pone el servidor (el token),
nunca el cliente. **Requiere tener una Entrada fichada y sin Salida** (sección 4): la carga queda
asociada a ese fichaje abierto, así que si no fichaste Entrada primero da 409.

### 6.1. Cargar productividad del día (EMPLEADO)

```bash
curl -X POST http://localhost:8080/api/productividad \
  -H "Content-Type: application/json" -H "Authorization: Bearer <token-del-empleado>" \
  -d '{"fecha": "2026-07-06", "pedidosEncargados": 50, "pedidosPreparados": 40, "bultosPreparados": 15}'
```

- `GET /api/productividad/mi-productividad` — el propio empleado ve su historial.

### 6.2. Consultas (ADMINISTRADOR/SUPERVISOR)

- `GET /api/productividad` — todo.
- `GET /api/productividad/empleado/{id}` — de un empleado.
- `GET /api/productividad/fecha?fecha=2026-07-06` — por fecha.

### 6.3. KPIs

Totales acumulados (pedidos, bultos) y cruce contra el objetivo semanal de tipo `PEDIDOS` vigente
(si el empleado tiene uno cargado — sección 7):

- `GET /api/productividad/kpi/me` — del propio empleado (EMPLEADO).
- `GET /api/productividad/kpi/{empleadoId}` — de un empleado puntual (ADMINISTRADOR/SUPERVISOR).

### 6.4. Promedios por rango de fechas

Promedio de pedidos/bultos por jornada, o por hora trabajada (cruza contra los fichajes de
Asistencia del rango), entre `inicio` y `fin` (`YYYY-MM-DD`, inclusive):

- `GET /api/productividad/promedios/me/jornada?inicio=...&fin=...` — propio (EMPLEADO).
- `GET /api/productividad/promedios/me/hora?inicio=...&fin=...` — propio (EMPLEADO).
- `GET /api/productividad/promedios/{empleadoId}/jornada?inicio=...&fin=...` — de un empleado (ADMINISTRADOR/SUPERVISOR).
- `GET /api/productividad/promedios/{empleadoId}/hora?inicio=...&fin=...` — de un empleado (ADMINISTRADOR/SUPERVISOR).

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

No hay un endpoint dedicado a "progreso de objetivo" — el cruce contra lo cargado se ve dentro del
KPI de Productividad (sección 6.3, `GET /api/productividad/kpi/me` o `/kpi/{empleadoId}`), que
incluye `objetivoPedidos`, `pedidosPendientesObjetivo` y `porcentajeCumplimiento` para el objetivo
`PEDIDOS` de la semana actual del empleado, si tiene uno cargado.

**Objetivos de tipo `DINERO`** no se cruzan contra nada — el sistema todavía no tiene ningún
módulo que registre ventas o dinero (queda como una meta informativa hasta que exista esa fuente
de datos).

- `GET /api/objetivos/{id}` — el objetivo.
- `GET /api/objetivos/empleado/{empleadoId}` — todos los objetivos de un empleado.

## 8. Migración desde Excel

`POST /api/excel/import` (sin restricción de rol hoy) — sube un `.xlsx`/`.xls` con hojas
`Empleados`/`Productividad_Diaria`/`Asistencia_Diaria` y carga/actualiza esos datos en la base,
matcheando empleados por nombre + apellido.

```bash
curl -X POST http://localhost:8080/api/excel/import \
  -F "file=@Dataset_RRHH_Deposito.xlsx"
```

## 9. Catálogo completo de endpoints

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
| GET | /api/empleados/{id}/foto | ADMINISTRADOR, SUPERADMIN, SUPERVISOR |
| POST | /api/attendance/{employeeId}/clock-in, /clock-out | EMPLEADO, ADMINISTRADOR, SUPERADMIN, SUPERVISOR (solo el propio id) |
| GET | /api/attendance | ADMINISTRADOR, SUPERADMIN, SUPERVISOR |
| GET | /api/attendance/empleado/{employeeId} | ADMINISTRADOR, SUPERADMIN, SUPERVISOR |
| GET | /api/attendance/pendientes, /{id}/foto | ADMINISTRADOR, SUPERADMIN, SUPERVISOR |
| POST | /api/attendance/{id}/revisar | ADMINISTRADOR, SUPERADMIN, SUPERVISOR |
| POST/PUT/DELETE | /api/work-schedules... | ADMINISTRADOR, SUPERADMIN |
| GET | /api/work-schedules... | ADMINISTRADOR, SUPERADMIN |
| POST | /api/productividad | EMPLEADO (propio, requiere Entrada fichada sin Salida) |
| GET | /api/productividad/mi-productividad | EMPLEADO (propio) |
| GET | /api/productividad, /empleado/{id}, /fecha | ADMINISTRADOR, SUPERVISOR |
| GET | /api/productividad/kpi/me | EMPLEADO (propio) |
| GET | /api/productividad/kpi/{empleadoId} | ADMINISTRADOR, SUPERVISOR |
| GET | /api/productividad/promedios/me/jornada, /me/hora | EMPLEADO (propio) |
| GET | /api/productividad/promedios/{empleadoId}/jornada, /hora | ADMINISTRADOR, SUPERVISOR |
| POST/PUT/DELETE | /api/objetivos | ADMINISTRADOR, SUPERADMIN |
| GET | /api/objetivos/{id}, /empleado/{empleadoId} | Cualquier rol logueado, pero EMPLEADO solo ve lo propio |
| POST | /api/excel/import | Abierto |

**Nota:** "Abierto" significa que no exige rol específico (alcanza con que el request llegue), no
que sea completamente público a nivel de red — hoy el proyecto tiene esos endpoints así porque
así estaban antes de sumar roles; no fue parte de lo pedido en esta ronda de cambios.

**Nota:** el módulo de Reportes (exportar productividad/asistencia a Excel/CSV) se retiró del
proyecto en esta ronda de cambios; no hay reemplazo todavía.
