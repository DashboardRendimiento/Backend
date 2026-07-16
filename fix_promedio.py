import re
path = 'src/main/java/com/rrhh/dashboard/registro_productividad/Dtos/PromedioProductividadDTO.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

constructor = '''
    public PromedioProductividadDTO(double promedioPedidos, double promedioBultos, long totalJornadasOHoras) {
        this.promedioPedidos = promedioPedidos;
        this.promedioBultos = promedioBultos;
        this.totalJornadasOHoras = totalJornadasOHoras;
    }
'''

content = content.replace('public PromedioProductividadDTO() {}', 'public PromedioProductividadDTO() {}\n' + constructor)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
