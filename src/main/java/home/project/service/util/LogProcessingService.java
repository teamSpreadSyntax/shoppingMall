package home.project.service.util;

import home.project.service.notification.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogProcessingService {

    private final ElkLogSenderService elkLogSenderService;
    private final WebSocketNotificationService webSocketNotificationService;

    /**
     * 로그를 ELK와 WebSocket으로 전송.
     *
     * @param logType    로그 타입 (예: kafka-logs, error-logs)
     * @param logMessage 로그 메시지
     */
    public void processLog(String logType, String logMessage) {
        // 1. ELK로 전송
        elkLogSenderService.sendLogToElk(logType, logMessage);

        // 2. WebSocket으로 전송
        String destination = "/topic/logs";
        webSocketNotificationService.sendNotification(destination, logMessage);

        System.out.println("Processed log: " + logType + " - " + logMessage);
    }
}
