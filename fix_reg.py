import os, re
def add_getset(path):
    if not os.path.exists(path): return
    with open(path, 'r', encoding='utf-8') as f: c = f.read()
    c = re.sub(r'@Data\s*', '', c)
    c = re.sub(r'@Builder\s*', '', c)
    c = re.sub(r'@AllArgsConstructor\s*', '', c)
    c = re.sub(r'@NoArgsConstructor\s*', '', c)
    fields = re.findall(r'private\s+([A-Za-z0-9_<>\\[\\]]+)\s+([a-zA-Z0-9_]+)\s*(?:=|;)', c)
    methods = "\n"
    for t, n in fields:
        if n == 'serialVersionUID': continue
        cap = n[0].upper() + n[1:]
        getter = f'get{cap}'
        setter = f'set{cap}'
        if not re.search(r'public\s+' + re.escape(t) + r'\s+' + getter + r'\s*\(', c):
            methods += f'    public {t} {getter}() {{ return this.{n}; }}\n'
        if not re.search(r'public\s+void\s+' + setter + r'\s*\(', c):
            methods += f'    public void {setter}({t} {n}) {{ this.{n} = {n}; }}\n'
    if methods.strip():
        last = c.rfind('}')
        if last != -1: c = c[:last] + methods + c[last:]
    with open(path, 'w', encoding='utf-8') as f: f.write(c)

add_getset('src/main/java/com/rrhh/dashboard/registro_productividad/Entity/registro_productividad.java')
