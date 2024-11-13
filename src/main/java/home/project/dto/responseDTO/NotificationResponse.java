package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {

    private Long id;

    private Long memberId;

    private String subject;

    private String description;

    private LocalDateTime createdAt;

    public NotificationResponse(Long id, Long memberId, String subject, String description, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.subject = subject;
        this.description = description;
        this.createdAt = createdAt;
    }
}
