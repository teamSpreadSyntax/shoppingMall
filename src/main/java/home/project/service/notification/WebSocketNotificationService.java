package home.project.service.notification;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
        System.out.println("Sent WebSocket notification to " + destination + ": " + message);
    }
}
