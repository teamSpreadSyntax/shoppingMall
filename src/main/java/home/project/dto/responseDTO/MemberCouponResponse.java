package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "회원 쿠폰 응답")
public class MemberCouponResponse {

    @Schema(description = "회원 쿠폰 ID", example = "1")
    private Long id;

    @Schema(description = "회원 이메일", example = "user@example.com")
    private String memberEmail;

    @Schema(description = "쿠폰 ID", example = "101")
    private Long couponId;

    @Schema(description = "할인율", example = "15")
    private Integer discountRate;

    @Schema(description = "발급일")
    private LocalDateTime issuedAt;

    @Schema(description = "사용일")
    private LocalDateTime usedAt;

    @Schema(description = "사용 여부", example = "false")
    private boolean isUsed;

    public MemberCouponResponse(Long id, String memberEmail, Long couponId, Integer discountRate, LocalDateTime issuedAt, LocalDateTime usedAt, boolean isUsed) {
        this.id = id;
        this.memberEmail = memberEmail;
        this.couponId = couponId;
        this.discountRate = discountRate;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
        this.isUsed = isUsed;
    }
}
