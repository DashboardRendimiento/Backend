import os

def fix_imports(path):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()

    changed = False
    if '@RequiredArgsConstructor' in content and 'import lombok.RequiredArgsConstructor;' not in content:
        content = content.replace('package', 'import lombok.RequiredArgsConstructor;\npackage', 1)
        changed = True
    if '@AllArgsConstructor' in content and 'import lombok.AllArgsConstructor;' not in content:
        content = content.replace('package', 'import lombok.AllArgsConstructor;\npackage', 1)
        changed = True
    if '@NoArgsConstructor' in content and 'import lombok.NoArgsConstructor;' not in content:
        content = content.replace('package', 'import lombok.NoArgsConstructor;\npackage', 1)
        changed = True
    if '@Data' in content and 'import lombok.Data;' not in content:
        content = content.replace('package', 'import lombok.Data;\npackage', 1)
        changed = True

    if changed:
        # Move imports below package declaration
        lines = content.split('\n')
        pkg = ""
        imports = []
        rest = []
        for line in lines:
            if line.startswith('package '):
                pkg = line
            elif line.startswith('import '):
                imports.append(line)
            else:
                if pkg or imports: # if we found package already
                    rest.append(line)
                else:
                    rest.append(line)
        
        final_content = f"{pkg}\n\n" + "\n".join(imports) + "\n" + "\n".join(rest)
        
        with open(path, 'w', encoding='utf-8') as f:
            f.write(final_content)

for root, dirs, files in os.walk('src'):
    for file in files:
        if file.endswith('.java'):
            fix_imports(os.path.join(root, file))
