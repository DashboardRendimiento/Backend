import os
import re

def process_java_file(filepath):
    if not filepath.endswith(".java"):
        return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Strip lombok
    content = re.sub(r'import lombok\..*?;\n', '', content)
    content = re.sub(r'@Data\s+', '', content)
    content = re.sub(r'@Builder\s+', '', content)
    content = re.sub(r'@NoArgsConstructor\s+', '', content)
    content = re.sub(r'@AllArgsConstructor\s+', '', content)
    
    # Find all fields
    fields = re.findall(r'private\s+([A-Za-z0-9_<>\[\]]+)\s+([a-zA-Z0-9_]+)\s*(?:=|;)', content)
    
    methods = ""
    for t, n in fields:
        if n == 'serialVersionUID': continue
        cap = n[0].upper() + n[1:]
        getter = f'get{cap}'
        setter = f'set{cap}'
        
        # Check if getter exists
        if not re.search(r'public\s+' + re.escape(t) + r'\s+' + getter + r'\s*\(', content):
            methods += f'    public {t} {getter}() {{ return this.{n}; }}\n'
        
        # Check if setter exists
        if not re.search(r'public\s+void\s+' + setter + r'\s*\(', content):
            methods += f'    public void {setter}({t} {n}) {{ this.{n} = {n}; }}\n'
            
    if methods:
        last_brace = content.rfind('}')
        if last_brace != -1:
            content = content[:last_brace] + methods + content[last_brace:]
            
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

base_dir = "src/main/java/com/rrhh/dashboard/registro_productividad"
for root, dirs, files in os.walk(base_dir):
    for f in files:
        process_java_file(os.path.join(root, f))
