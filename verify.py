import os

path = 'src/main/java/com/rrhh/dashboard/Empleados/controllers/GlobalExceptionHandler.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# Verify the fix is still there
if "handleAccessDeniedException" in content:
    print("Fix confirmed.")
else:
    print("Fix missing.")
