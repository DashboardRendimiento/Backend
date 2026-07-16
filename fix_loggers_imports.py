import re
def add_logger(path):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    if 'import org.slf4j.Logger;' not in content:
        content = re.sub(r'(package[^\n]+;)', r'\1\nimport org.slf4j.Logger;\nimport org.slf4j.LoggerFactory;\n', content)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

add_logger('src/main/java/com/rrhh/dashboard/registro_productividad/Service/ProductivadPromedios.java')
add_logger('src/main/java/com/rrhh/dashboard/registro_productividad/Controllers/ProductividadController.java')
