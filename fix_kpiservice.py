import os, re
def fix_service(path):
    if not os.path.exists(path): return
    with open(path, 'r', encoding='utf-8') as f:
        c = f.read()
    c = re.sub(r'@Slf4j\s*', '', c)
    if 'import org.slf4j.Logger;' not in c:
        c = re.sub(r'(package[^\n]+;)', r'\1\nimport org.slf4j.Logger;\nimport org.slf4j.LoggerFactory;\n', c)
    class_match = re.search(r'public\s+class\s+([A-Za-z0-9_]+)', c)
    if class_match and 'Logger log =' not in c:
        cname = class_match.group(1)
        c = re.sub(r'(public\s+class\s+' + cname + r'[\s\S]*?\{)', r'\1\n    private static final Logger log = LoggerFactory.getLogger(' + cname + r'.class);\n', c, count=1)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(c)

fix_service('src/main/java/com/rrhh/dashboard/registro_productividad/Service/ProductividadKPIService.java')
