import os
import re

def add_getters_setters(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Remove lombok annotations
        content = re.sub(r'@Data\s+', '', content)
        content = re.sub(r'@Builder\s+', '', content)
        content = re.sub(r'@AllArgsConstructor\s+', '', content)
        content = re.sub(r'@NoArgsConstructor\s+', '', content)
        content = re.sub(r'import lombok\..*?;\n', '', content)

        # Find fields
        fields = re.findall(r'private\s+([A-Za-z0-9_<>\[\]]+)\s+([a-zA-Z0-9_]+)\s*(?:=|;)', content)
        
        methods = ""
        for f_type, f_name in fields:
            if f_name == 'serialVersionUID': continue
            cap_name = f_name[0].upper() + f_name[1:]
            
            # Getter
            if not f'get{cap_name}(' in content:
                methods += f'    public {f_type} get{cap_name}() {{ return this.{f_name}; }}\n'
            
            # Setter
            if not f'set{cap_name}(' in content:
                methods += f'    public void set{cap_name}({f_type} {f_name}) {{ this.{f_name} = {f_name}; }}\n'
            
        # Add basic constructors
        class_match = re.search(r'public class\s+([A-Za-z0-9_]+)', content)
        if class_match:
            c_name = class_match.group(1)
            # Empty constructor
            methods += f'    public {c_name}() {{}}\n'
            # Full constructor (if many fields)
            args = ", ".join([f"{t} {n}" for t, n in fields])
            assigns = "".join([f"        this.{n} = {n};\n" for t, n in fields])
            methods += f'    public {c_name}({args}) {{\n{assigns}    }}\n'
            
        last_brace = content.rfind('}')
        if last_brace != -1:
            content = content[:last_brace] + methods + content[last_brace:]
            
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f'Added getters/setters/constructors to {file_path}')
    except Exception as e:
        print(f'Error processing {file_path}: {e}')

add_getters_setters('src/main/java/com/rrhh/dashboard/Migracion/Dtos/AsistenciaDiariaDTO.java')
add_getters_setters('src/main/java/com/rrhh/dashboard/registro_productividad/Entity/registro_productividad.java')
add_getters_setters('src/main/java/com/rrhh/dashboard/registro_productividad/Dtos/PromedioProductividadDTO.java')
add_getters_setters('src/main/java/com/rrhh/dashboard/registro_productividad/Dtos/ProductividadDiariaDTO.java')
