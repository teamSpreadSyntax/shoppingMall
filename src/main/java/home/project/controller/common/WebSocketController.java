package home.project.controller.common;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    // 클라이언트로부터 메시지를 받기 위한 엔드포인트
    @MessageMapping("/log")
    @SendTo("/topic/logs")
    public String sendLog(String message) {
        // 받은 메시지를 다시 클라이언트로 브로드캐스팅
        return message;
    }
}
