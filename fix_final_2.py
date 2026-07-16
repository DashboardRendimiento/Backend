import re
# Fix AsistenciaExcelService
path_excel = 'src/main/java/com/rrhh/dashboard/Migracion/service/AsistenciaExcelService.java'
with open(path_excel, 'r', encoding='utf-8') as f:
    c = f.read()
c = re.sub(r'ad\.setHorasTrabajadas\(horasTrabajadas\);\s*', '', c)
c = re.sub(r'ad\.setMinutosTardanza\(minutosTardanza\);\s*', '', c)
c = re.sub(r'ad\.setHorasExtra\(horasExtra\);\s*', '', c)
c = re.sub(r'ad\.setFecha\(fecha\);\s*', '', c)
with open(path_excel, 'w', encoding='utf-8') as f: f.write(c)
