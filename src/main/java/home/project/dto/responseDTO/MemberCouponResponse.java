package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class MemberCouponResponse {

    private Long id;

    private String memberEmail;

    private Long couponId;

    private Integer discountRate;

    private LocalDateTime issuedAt;

    private LocalDateTime usedAt;

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
