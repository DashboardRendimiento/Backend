package com.rrhh.dashboard.Asistencia.services;

public record ResultadoComparacionFacial(
        boolean rostroDetectadoReferencia,
        boolean rostroDetectadoCaptura,
        double similitud,
        Double distancia
) {
    public boolean seDetectaronAmbosRostros() {
        return rostroDetectadoReferencia && rostroDetectadoCaptura;
    }
}
