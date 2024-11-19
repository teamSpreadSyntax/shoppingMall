package home.project.controller.common;

import home.project.service.notification.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor // LogService 주입을 위해 Lombok 사용
public class WebSocketController {

    private final WebSocketNotificationService webSocketNotificationService;

    // 클라이언트로부터 메시지를 받기 위한 엔드포인트
    @MessageMapping("/log")
    public void handleLog(@Payload String logMessage) {
        webSocketNotificationService.sendNotification("/topic/logs", logMessage); // WebSocket으로 메시지 전송
    }

    @MessageMapping("/send-log")
    public void handleSendLog(@Payload String logMessage) {
        webSocketNotificationService.sendNotification("/topic/logs", logMessage); // WebSocket 알림 전송
    }
}
