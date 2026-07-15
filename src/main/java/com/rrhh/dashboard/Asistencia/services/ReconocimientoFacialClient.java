package com.rrhh.dashboard.Asistencia.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * Llama al microservicio Python (face-recognition-service) que compara dos
 * fotos de rostro. Nunca propaga una excepcion de infraestructura (timeout,
 * conexion rechazada, respuesta invalida) — devuelve Optional.empty() en
 * cualquiera de esos casos, para que AttendanceService trate una falla de
 * este servicio igual que una similitud insuficiente: el fichaje se
 * registra igual, queda pendiente de revision humana.
 */
@Component
public class ReconocimientoFacialClient {

    private static final Logger log = LoggerFactory.getLogger(ReconocimientoFacialClient.class);

    private final RestClient restClient;

    public ReconocimientoFacialClient(@Value("${app.reconocimiento-facial.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public Optional<ResultadoComparacionFacial> comparar(byte[] fotoReferencia, byte[] fotoCaptura) {
        try {
            MultiValueMap<String, Object> partes = new LinkedMultiValueMap<>();
            partes.add("referencia", new ByteArrayResource(fotoReferencia) {
                @Override
                public String getFilename() {
                    return "referencia.jpg";
                }
            });
            partes.add("captura", new ByteArrayResource(fotoCaptura) {
                @Override
                public String getFilename() {
                    return "captura.jpg";
                }
            });

            RespuestaComparacion respuesta = restClient.post()
                    .uri("/comparar-rostros")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(partes)
                    .retrieve()
                    .body(RespuestaComparacion.class);

            if (respuesta == null) {
                return Optional.empty();
            }
            return Optional.of(new ResultadoComparacionFacial(
                    respuesta.rostroDetectadoReferencia(),
                    respuesta.rostroDetectadoCaptura(),
                    respuesta.similitud(),
                    respuesta.distancia()));
        } catch (Exception e) {
            log.warn("No se pudo comparar los rostros (face-recognition-service no disponible o fallo): {}",
                    e.getMessage());
            return Optional.empty();
        }
    }

    private record RespuestaComparacion(
            boolean rostroDetectadoReferencia,
            boolean rostroDetectadoCaptura,
            double similitud,
            Double distancia
    ) {
    }
}
