package com.rrhh.dashboard.Asistencia.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.Map;

@Component
public class AttendanceBroadcaster {

    private static final String TOPIC = "/topic/attendance";
    private final SimpMessagingTemplate messagingTemplate;

    public AttendanceBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastAttendanceUpdate(Long employeeId, String type) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendPayload(employeeId, type);
                }
            });
        } else {
            sendPayload(employeeId, type);
        }
    }

    private void sendPayload(Long employeeId, String type) {
        Map<String, Object> payload = Map.of(
            "employeeId", employeeId,
            "type", type
        );
        messagingTemplate.convertAndSend(TOPIC, payload);
    }
}