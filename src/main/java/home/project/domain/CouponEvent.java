package home.project.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public CouponEvent(
            @JsonProperty("eventType") String eventType,
            @JsonProperty("coupon") Coupon coupon,
            @JsonProperty("member") Member member,
            @JsonProperty("product") Product product,
            @JsonProperty("eventTime") LocalDateTime eventTime
    ) {
        this.eventType = eventType;
        this.coupon = coupon;
        this.member = member;
        this.product = product;
        this.eventTime = eventTime != null ? eventTime : LocalDateTime.now();
    }

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