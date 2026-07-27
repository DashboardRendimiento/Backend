import os
import re

def add_logger(path, class_name):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if 'import org.slf4j.Logger;' not in content:
        content = re.sub(r'package ([a-zA-Z0-9_\.]+);', r'package \1;\n\nimport org.slf4j.Logger;\nimport org.slf4j.LoggerFactory;', content)
    
    if 'private static final Logger log' not in content:
        content = re.sub(r'public class ' + class_name + r' (?:implements [a-zA-Z0-9_]+ )?\{', r'public class ' + class_name + r' {\n    private static final Logger log = LoggerFactory.getLogger(' + class_name + r'.class);', content)
    
    content = re.sub(r'@Slf4j\s*', '', content)
    
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

add_logger('src/main/java/com/rrhh/dashboard/registro_productividad/Service/ProductivadPromedios.java', 'ProductivadPromedios')
add_logger('src/main/java/com/rrhh/dashboard/registro_productividad/Service/ProductividadKPIService.java', 'ProductividadKPIService')
add_logger('src/main/java/com/rrhh/dashboard/registro_productividad/Service/ProductividadService.java', 'ProductividadService')
add_logger('src/main/java/com/rrhh/dashboard/registro_productividad/websocket/ProductividadKpiBroadcaster.java', 'ProductividadKpiBroadcaster')
