package com.rrhh.dashboard.registro_productividad.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rrhh.dashboard.registro_productividad.Dtos.ProductividadKPIDTO;
import com.rrhh.dashboard.registro_productividad.Service.ProductividadKPIService;
import com.rrhh.dashboard.registro_productividad.events.ProductividadRegistradaEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Recalcula el KPI del empleado y lo publica en
 * {@code /topic/productividad/{empleadoId}}. Escucha AFTER_COMMIT: si
 * escuchara el evento en cuanto se publica (dentro de la transaccion de
 * guardar), el KPI recalculado no veria todavia el registro recien
 * guardado en otra conexion/hilo.
 */
@Component
public class ProductividadKpiBroadcaster {
    private static final Logger log = LoggerFactory.getLogger(ProductividadKpiBroadcaster.class);

    private static final String TOPIC_PREFIX = "/topic/productividad/";

    private final ProductividadKPIService kpiService;
    private final SimpMessagingTemplate messagingTemplate;
    public ProductividadKpiBroadcaster(ProductividadKPIService kpiService, SimpMessagingTemplate messagingTemplate) {
        this.kpiService = kpiService;
        this.messagingTemplate = messagingTemplate;
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductividadRegistrada(ProductividadRegistradaEvent event) {
        ProductividadKPIDTO kpi = kpiService.obtenerKPI(event.empleadoId());
        messagingTemplate.convertAndSend(TOPIC_PREFIX + event.empleadoId(), kpi);
    }
}
