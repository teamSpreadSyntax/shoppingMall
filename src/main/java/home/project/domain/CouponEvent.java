package home.project.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CouponEvent {
    private String eventType;
    private Coupon coupon;
    private Member member;
    private Product product;
    private LocalDateTime eventTime;

    public CouponEvent(String eventType, Coupon coupon) {
        this.eventType = eventType;
        this.coupon = coupon;
        this.eventTime = LocalDateTime.now();
    }

    public CouponEvent(String eventType, Coupon coupon, Member member) {
        this(eventType, coupon);
        this.member = member;
    }

    public CouponEvent(String eventType, Coupon coupon, Product product) {
        this(eventType, coupon);
        this.product = product;
    }
}