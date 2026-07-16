import re
with open('src/main/java/com/rrhh/dashboard/Migracion/service/AsistenciaExcelService.java', 'r', encoding='utf-8') as f:
    content = f.read()

replacement = '''
        AsistenciaDiaria ad = new AsistenciaDiaria();
        ad.setIdEmpleado(idEmpleado);
        ad.setNombre(nombre);
        ad.setEstado(estado);
        ad.setHorasTrabajadas(horasTrabajadas);
        ad.setMinutosTardanza(minutosTardanza);
        ad.setHorasExtra(horasExtra);
        ad.setFecha(fecha);
        ad.setEmpleado(empleado);
        return ad;
'''

content = re.sub(r'return AsistenciaDiaria\.builder\(\)[\s\S]*?\.build\(\);', replacement, content)

with open('src/main/java/com/rrhh/dashboard/Migracion/service/AsistenciaExcelService.java', 'w', encoding='utf-8') as f:
    f.write(content)
