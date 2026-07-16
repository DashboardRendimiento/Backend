import os
import re

def fix_file(path, is_service=False):
    if not os.path.exists(path):
        return
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Remove Lombok annotations
    content = re.sub(r'@Data\s*', '', content)
    content = re.sub(r'@Builder\s*', '', content)
    content = re.sub(r'@AllArgsConstructor\s*', '', content)
    content = re.sub(r'@NoArgsConstructor\s*', '', content)
    content = re.sub(r'@Slf4j\s*', '', content)

    # Fix Slf4j logger if needed
    if is_service:
        content = content.replace('import lombok.extern.slf4j.Slf4j;', 'import org.slf4j.Logger;\nimport org.slf4j.LoggerFactory;')
        class_match = re.search(r'public\s+class\s+([A-Za-z0-9_]+)', content)
        if class_match:
            cname = class_match.group(1)
            if 'Logger log =' not in content:
                content = re.sub(r'(public\s+class\s+' + cname + r'[\s\S]*?\{)', r'\1\n    private static final Logger log = LoggerFactory.getLogger(' + cname + r'.class);\n', content, count=1)

    # Find fields
    fields = re.findall(r'private\s+([A-Za-z0-9_<>\[\]]+)\s+([a-zA-Z0-9_]+)\s*(?:=|;)', content)

    # Find class name for constructors
    class_match = re.search(r'public\s+class\s+([A-Za-z0-9_]+)', content)
    
    methods = "\n"
    if class_match and not is_service:
        cname = class_match.group(1)
        # Empty constructor
        if f'public {cname}()' not in content:
            methods += f'    public {cname}() {{}}\n'
        # All args constructor
        args = ", ".join([f"{t} {n}" for t, n in fields])
        assigns = "".join([f"        this.{n} = {n};\n" for t, n in fields])
        if f'public {cname}(' not in content or fields:
            # simple check if full constructor exists (rough)
            if len(fields) > 0 and args.split(',')[0] not in content:
                methods += f'    public {cname}({args}) {{\n{assigns}    }}\n'

    for t, n in fields:
        if n == 'serialVersionUID': continue
        cap = n[0].upper() + n[1:]
        getter = f'get{cap}'
        setter = f'set{cap}'
        
        # Check if exists
        if not re.search(r'public\s+' + re.escape(t) + r'\s+' + getter + r'\s*\(', content):
            methods += f'    public {t} {getter}() {{ return this.{n}; }}\n'
        if not re.search(r'public\s+void\s+' + setter + r'\s*\(', content):
            methods += f'    public void {setter}({t} {n}) {{ this.{n} = {n}; }}\n'

    if not is_service and methods.strip():
        last_brace = content.rfind('}')
        if last_brace != -1:
            content = content[:last_brace] + methods + content[last_brace:]

    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

fix_file('src/main/java/com/rrhh/dashboard/Migracion/Dtos/ExcelRowDTO.java')
fix_file('src/main/java/com/rrhh/dashboard/Empleados/Entity/Empleados.java')
fix_file('src/main/java/com/rrhh/dashboard/registro_productividad/Entity/AsistenciaDiaria.java')
fix_file('src/main/java/com/rrhh/dashboard/PlantillaHoras/Entity/PlantillaHoras.java')
fix_file('src/main/java/com/rrhh/dashboard/PlantillaHoras/Dtos/PlantillaHorasDTO.java')
fix_file('src/main/java/com/rrhh/dashboard/Migracion/Dtos/AsistenciaDiariaDTO.java')
fix_file('src/main/java/com/rrhh/dashboard/registro_productividad/Entity/registro_productividad.java')
fix_file('src/main/java/com/rrhh/dashboard/registro_productividad/Dtos/PromedioProductividadDTO.java')
fix_file('src/main/java/com/rrhh/dashboard/registro_productividad/Dtos/ProductividadDiariaDTO.java')

fix_file('src/main/java/com/rrhh/dashboard/Migracion/service/AsistenciaExcelService.java', True)
fix_file('src/main/java/com/rrhh/dashboard/service/ExcelDataService.java', True)
fix_file('src/main/java/com/rrhh/dashboard/registro_productividad/Controllers/ProductividadController.java', True)
fix_file('src/main/java/com/rrhh/dashboard/registro_productividad/Service/ProductivadPromedios.java', True)
