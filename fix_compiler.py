import os

def fix_dto(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    out = []
    for line in lines:
        if 'getPedidosPendientes' in line or 'setPedidosPendientes' in line:
            continue
        if 'getPedidosEncargados' in line or 'setPedidosEncargados' in line:
            if 'ProductividadDiariaDTO' not in filepath: # keep it in DTO, remove from entity if not there
                if 'registro_productividad' in filepath and 'private Integer pedidosEncargados;' not in open(filepath,'r').read():
                    continue
        if 'getFechaCarga' in line or 'setFechaCarga' in line:
            if 'registro_productividad' in filepath and 'private LocalDateTime fechaCarga;' not in open(filepath,'r').read():
                continue
        if 'public ProductividadDiariaDTO() {}' in line:
            if any('public ProductividadDiariaDTO() {}' in o for o in out):
                continue
        if 'public registro_productividad() {}' in line:
            if any('public registro_productividad() {}' in o for o in out):
                continue
        
        out.append(line)
        
    with open(filepath, 'w', encoding='utf-8') as f:
        f.writelines(out)

fix_dto('src/main/java/com/rrhh/dashboard/registro_productividad/Dtos/ProductividadDiariaDTO.java')
fix_dto('src/main/java/com/rrhh/dashboard/registro_productividad/Entity/registro_productividad.java')
