package home.project.dto.responseDTO;

import home.project.domain.notification.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDetailResponse {
    private NotificationType notificationType;
    private String description;
    private LocalDateTime createdAt;

    public NotificationDetailResponse(NotificationType notificationType, String description, LocalDateTime createdAt) {
        this.notificationType = notificationType;
        this.description = description;
        this.createdAt = createdAt;
    }
}
