package com.rrhh.dashboard.Asistencia.exceptions;

import com.rrhh.dashboard.Asistencia.Entity.EstadoVerificacionFacial;

/**
 * Se intento revisar manualmente un fichaje que no esta
 * PENDIENTE_REVISION (ya se verifico automaticamente, o ya se reviso
 * antes).
 */
public class RevisionNoAplicableException extends RuntimeException {

    public RevisionNoAplicableException(Long attendanceRecordId, EstadoVerificacionFacial estadoActual) {
        super("El fichaje " + attendanceRecordId + " no esta pendiente de revision (estado actual: "
                + estadoActual + ")");
    }
}
