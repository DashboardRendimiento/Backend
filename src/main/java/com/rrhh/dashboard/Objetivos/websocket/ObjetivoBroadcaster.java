package com.rrhh.dashboard.Objetivos.websocket;

import com.rrhh.dashboard.Objetivos.dtos.ObjetivoResponse;
import com.rrhh.dashboard.Objetivos.events.ObjetivoAsignadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ObjetivoBroadcaster {
    private static final Logger log = LoggerFactory.getLogger(ObjetivoBroadcaster.class);
    private static final String TOPIC_PREFIX = "/topic/objetivos/";

    private final SimpMessagingTemplate messagingTemplate;

    public ObjetivoBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onObjetivoAsignado(ObjetivoAsignadoEvent event) {
        log.debug("Broadcasting objetivo asignado for employee {}", event.empleadoId());
        messagingTemplate.convertAndSend(TOPIC_PREFIX + event.empleadoId(), event.objetivo());
    }
}
