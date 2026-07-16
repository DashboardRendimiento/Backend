import os
import re

def fix_lombok_logger(file_path, class_name):
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        if '@Slf4j' in content:
            content = content.replace('@Slf4j', '')
            content = content.replace('import lombok.extern.slf4j.Slf4j;', 'import org.slf4j.Logger;\nimport org.slf4j.LoggerFactory;')
            logger_field = f'\n    private static final Logger log = LoggerFactory.getLogger({class_name}.class);\n'
            # insert logger after class definition
            content = re.sub(r'(public class ' + class_name + r'\s*(?:extends [^{]+)?(?:implements [^{]+)?\{)', r'\1' + logger_field, content)
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f'Fixed logger in {class_name}')
    except Exception as e:
        print(f'Error fixing logger in {file_path}: {e}')

fix_lombok_logger('src/main/java/com/rrhh/dashboard/Migracion/service/AsistenciaExcelService.java', 'AsistenciaExcelService')
fix_lombok_logger('src/main/java/com/rrhh/dashboard/service/ExcelDataService.java', 'ExcelDataService')
