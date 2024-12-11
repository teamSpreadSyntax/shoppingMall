package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "회원 이벤트 응답")
public class MemberEventResponse {

    @Schema(description = "회원 이벤트 ID", example = "1")
    private Long id;

    @Schema(description = "회원 이메일", example = "user@example.com")
    private String memberEmail;

    @Schema(description = "이벤트 ID", example = "202")
    private Long eventId;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    public MemberEventResponse(Long id, String memberEmail, Long eventId, LocalDateTime createdAt) {
        this.id = id;
        this.memberEmail = memberEmail;
        this.eventId = eventId;
        this.createdAt = createdAt;
    }
}
