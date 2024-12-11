package home.project.dto.responseDTO;

import home.project.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "배송 메시지 응답")
public class ShippingMessageResponse {

    @Schema(description = "메시지 ID", example = "1")
    private Long id;

    @Schema(description = "메시지 내용", example = "배송이 시작되었습니다.")
    private String message;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "회원 정보")
    private Member member;

    public ShippingMessageResponse(Long id, String message, LocalDateTime createdAt, Member member) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.member = member;
    }
}
