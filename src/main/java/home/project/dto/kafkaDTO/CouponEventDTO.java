package home.project.dto.kafkaDTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CouponEventDTO {
    private String eventType;
    private Long couponId;
    private Long memberId;
    private Long productId;
    private LocalDateTime eventTime;

    @JsonCreator
    public CouponEventDTO(
            @JsonProperty("eventType") String eventType,
            @JsonProperty("couponId") Long couponId,
            @JsonProperty("memberId") Long memberId,
            @JsonProperty("productId") Long productId,
            @JsonProperty("eventTime") LocalDateTime eventTime
    ) {
        this.eventType = eventType;
        this.couponId = couponId;
        this.memberId = memberId;
        this.productId = productId;
        this.eventTime = eventTime != null ? eventTime : LocalDateTime.now();
    }

    public CouponEventDTO(String eventType, Long couponId) {
        this(eventType, couponId, null, null, LocalDateTime.now());
    }

    public CouponEventDTO(String eventType, Long couponId, Long memberId) {
        this(eventType, couponId, memberId, null, LocalDateTime.now());
    }

    public CouponEventDTO(String eventType, Long couponId, Long memberId, Long productId) {
        this(eventType, couponId, memberId, productId, LocalDateTime.now());
    }
}