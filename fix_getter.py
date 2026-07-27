import os

path = 'src/main/java/com/rrhh/dashboard/Empleados/Entity/Empleados.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

target = "public byte[] getFotoReferencia() { return this.fotoReferencia; }"
replacement = "    @JsonIgnore\n    public byte[] getFotoReferencia() { return this.fotoReferencia; }"

if target in content:
    content = content.replace(target, replacement)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Added @JsonIgnore to getter.")
else:
    print("Target not found.")
