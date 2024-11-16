package home.project.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
        System.out.println("Sent WebSocket notification to " + destination + ": " + message);
    }

    @MessageMapping("/chat/send")
    public void sendMsg(@Payload Map<String,Object> data){
        messagingTemplate.convertAndSend("/topic/1",data);
    }
}
