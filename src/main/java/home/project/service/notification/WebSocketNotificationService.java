package home.project.service.notification;

import home.project.domain.notification.Notification;
import home.project.dto.responseDTO.NotificationResponse;
import home.project.response.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
        System.out.println("Sent WebSocket notification to " + destination + ": " + message);
    }

    public void sendNotificationToUser(String username, NotificationResponse notificationResponse) {
        CustomResponseEntity<NotificationResponse> customResponseEntity = new CustomResponseEntity<>(notificationResponse, username+"님에게 "+notificationResponse.getDescription(), HttpStatus.OK);
        messagingTemplate.convertAndSendToUser(
                username,             // 수신자 ID
                "/queue/notifications", // 개인별 큐
                customResponseEntity              // 메시지 내용
        );
    }
    //구독시 stompClient.subscribe('/user/queue/notifications', ...); 이런 방식으로 구독하면 실제 내부적으로는 /user/{email}/queue/notifications 같은 형태로 destination이 변환됨

}
