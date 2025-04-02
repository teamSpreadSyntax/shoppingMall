package home.project.controller.common;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/log")
    @SendTo("/topic/logs")
    public String sendLog(String message) {
        return message;
    }
}
