import os
import glob

def fix_preauth(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Replace hasRole('EMPLEADO') with hasAnyRole('EMPLEADO', 'SUPERVISOR', 'ADMINISTRADOR', 'SUPERADMIN')
    content = content.replace("hasRole('EMPLEADO')", "hasAnyRole('EMPLEADO', 'SUPERVISOR', 'ADMINISTRADOR', 'SUPERADMIN')")
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

for root, _, files in os.walk('src/main/java/com/rrhh/dashboard/'):
    for file in files:
        if file.endswith('.java'):
            fix_preauth(os.path.join(root, file))

print("Fixed PreAuthorize annotations.")
