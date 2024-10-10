package home.project.dto.responseDTO;

import home.project.domain.Coupon;
import home.project.domain.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class MemberCouponResponse {

    private Long id;

    private String memberEmail;

    private Long couponId;

    private LocalDateTime issuedAt;

    private LocalDateTime usedAt;

    private boolean isUsed;

    public MemberCouponResponse(Long id, String memberEmail, Long couponId, LocalDateTime issuedAt, LocalDateTime usedAt, boolean isUsed){
        this.id = id;
        this.memberEmail = memberEmail;
        this.couponId = couponId;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
        this.isUsed = isUsed;
    }
}
