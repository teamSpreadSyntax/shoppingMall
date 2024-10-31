package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter

@Setter
public class NotificationDetailResponse {
    private String subject;
    private String description;
    private LocalDateTime createdAt;

    public NotificationDetailResponse(String subject, String description, LocalDateTime createdAt) {
        this.subject = subject;
        this.description = description;
        this.createdAt = createdAt;
    }
}
