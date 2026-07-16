import os
import re

def add_getters_setters(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Remove lombok annotations
        content = re.sub(r'@Data\s+', '', content)
        content = re.sub(r'@Builder\s+', '', content)
        content = re.sub(r'import lombok\..*?;\n', '', content)

        # Find fields
        fields = re.findall(r'private\s+([A-Za-z0-9_<>\[\]]+)\s+([a-zA-Z0-9_]+)\s*(?:=|;)', content)
        
        methods = ""
        for f_type, f_name in fields:
            if f_name == 'serialVersionUID': continue
            cap_name = f_name[0].upper() + f_name[1:]
            
            # Getter
            methods += f'    public {f_type} get{cap_name}() {{ return this.{f_name}; }}\n'
            
            # Setter
            methods += f'    public void set{cap_name}({f_type} {f_name}) {{ this.{f_name} = {f_name}; }}\n'
            
        # Also handle Builder for AsistenciaDiaria (just add a basic builder if needed, but it's easier to just use no-args constructor)
        # Wait, AsistenciaDiaria uses AsistenciaDiaria.builder()...build(). We might need to manually fix that in AsistenciaExcelService.
            
        # Append methods before the last closing brace
        last_brace = content.rfind('}')
        if last_brace != -1:
            content = content[:last_brace] + methods + content[last_brace:]
            
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f'Added getters/setters to {file_path}')
    except Exception as e:
        print(f'Error processing {file_path}: {e}')

add_getters_setters('src/main/java/com/rrhh/dashboard/Migracion/Dtos/ExcelRowDTO.java')
add_getters_setters('src/main/java/com/rrhh/dashboard/Empleados/Entity/Empleados.java')
add_getters_setters('src/main/java/com/rrhh/dashboard/registro_productividad/Entity/AsistenciaDiaria.java')
add_getters_setters('src/main/java/com/rrhh/dashboard/PlantillaHoras/Entity/PlantillaHoras.java')
add_getters_setters('src/main/java/com/rrhh/dashboard/PlantillaHoras/Dtos/PlantillaHorasDTO.java')
