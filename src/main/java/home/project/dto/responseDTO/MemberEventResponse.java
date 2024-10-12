package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class MemberEventResponse {

    private Long id;

    private String memberEmail;

    private Long eventId;

    private LocalDateTime createdAt;


    public MemberEventResponse(Long id, String memberEmail, Long eventId, LocalDateTime createdAt) {
        this.id = id;
        this.memberEmail = memberEmail;
        this.eventId = eventId;
        this.createdAt = createdAt;
    }
}
