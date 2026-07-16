import os, re
def fix_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    content = re.sub(r'@Data\s*', '', content)
    content = re.sub(r'@Builder\s*', '', content)
    content = re.sub(r'@AllArgsConstructor\s*', '', content)
    content = re.sub(r'@NoArgsConstructor\s*', '', content)
    fields = re.findall(r'private\s+([A-Za-z0-9_<>\[\]]+)\s+([a-zA-Z0-9_]+)\s*(?:=|;)', content)
    methods = "\n"
    for t, n in fields:
        if n == 'serialVersionUID': continue
        cap = n[0].upper() + n[1:]
        getter = f'get{cap}'
        setter = f'set{cap}'
        if not re.search(r'public\s+' + re.escape(t) + r'\s+' + getter + r'\s*\(', content):
            methods += f'    public {t} {getter}() {{ return this.{n}; }}\n'
        if not re.search(r'public\s+void\s+' + setter + r'\s*\(', content):
            methods += f'    public void {setter}({t} {n}) {{ this.{n} = {n}; }}\n'
    if methods.strip():
        last_brace = content.rfind('}')
        if last_brace != -1:
            content = content[:last_brace] + methods + content[last_brace:]
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
fix_file('src/main/java/com/rrhh/dashboard/Empleados/dtos/EmpleadoDTO.java')
