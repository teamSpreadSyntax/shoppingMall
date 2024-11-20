//package home.project.service.util;
//
//import home.project.service.notification.WebSocketNotificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class LogService {
//    private final WebSocketNotificationService notificationService;
//
//    public void sendLogMessage(String log) {
//        // 목적지 설정 (예: 특정 채널에 로그 발송)
//        String destination = "/topic/logs";
//
//        // 로그 메시지 WebSocket 전송
//        notificationService.sendNotification(destination, log);
//        System.out.println("Log sent to WebSocket: " + log);
//    }
//}
