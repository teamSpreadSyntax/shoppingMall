package home.project.dto.responseDTO;

import home.project.domain.notification.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {

    private Long id;

    private Long memberId;

    private NotificationType notificationType;

    private String description;

    private Boolean isRead;

    private LocalDateTime createdAt;


    public NotificationResponse(Long id, Long memberId, NotificationType notificationType, String description, Boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.notificationType = notificationType;
        this.description = description;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
}
