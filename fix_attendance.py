import os

path = 'src/main/java/com/rrhh/dashboard/Asistencia/Entity/AttendanceRecord.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

target = "public byte[] getFotoCapturada() { return fotoCapturada; }"
replacement = "    @com.fasterxml.jackson.annotation.JsonIgnore\n    public byte[] getFotoCapturada() { return fotoCapturada; }"

if target in content:
    content = content.replace(target, replacement)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Added @JsonIgnore to getFotoCapturada.")
else:
    print("Target not found. Let's see the getters.")
    import re
    getters = re.findall(r'public byte\[\] getFotoCapturada\(\).*', content)
    if getters:
        content = content.replace(getters[0], "    @com.fasterxml.jackson.annotation.JsonIgnore\n    " + getters[0])
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        print("Replaced:", getters[0])
    else:
        print("No getter found.")
