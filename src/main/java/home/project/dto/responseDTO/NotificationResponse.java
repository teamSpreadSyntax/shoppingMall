package home.project.dto.responseDTO;

import home.project.domain.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {

    private Long id;

    private Member member;

    private String subject;

    private String description;

    private LocalDateTime createdAt;

    public NotificationResponse(Long id, Member member, String subject, String description, LocalDateTime createdAt) {
        this.id = id;
        this.member = member;
        this.subject = subject;
        this.description = description;
        this.createdAt = createdAt;
    }
}
