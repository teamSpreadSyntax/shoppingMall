package home.project.dto.responseDTO;

import home.project.domain.member.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShippingMessageResponse {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private Member member;

    public ShippingMessageResponse(Long id, String message, LocalDateTime createdAt, Member member) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.member = member;
    }
}
