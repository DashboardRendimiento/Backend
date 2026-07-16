import os
import re

def fix_required_args(path):
    with open(path, 'r', encoding='utf-8-sig') as f:
        content = f.read()

    if '@RequiredArgsConstructor' not in content:
        return

    content = re.sub(r'@RequiredArgsConstructor\s*', '', content)

    # find class name
    class_match = re.search(r'public\s+class\s+([A-Za-z0-9_]+)', content)
    if not class_match: return
    cname = class_match.group(1)

    # find private final fields
    fields = re.findall(r'private\s+final\s+([A-Za-z0-9_<>, \?]+)\s+([a-zA-Z0-9_]+)\s*;', content)
    if not fields:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        return

    args = ', '.join([f'{t} {n}' for t, n in fields])
    assigns = '\n'.join([f'        this.{n} = {n};' for t, n in fields])
    
    constructor = f'\n    public {cname}({args}) {{\n{assigns}\n    }}\n'
    
    # insert after the last private final field
    last_field_idx = 0
    for t, n in fields:
        idx = content.find(f'{n};')
        if idx > last_field_idx:
            last_field_idx = idx
            
    insert_pos = content.find(';', last_field_idx) + 1
    content = content[:insert_pos] + constructor + content[insert_pos:]
    
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

for root, dirs, files in os.walk('src'):
    for file in files:
        if file.endswith('.java'):
            fix_required_args(os.path.join(root, file))
