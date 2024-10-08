package home.project.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.CouponEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventListener {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "coupon-events", groupId = "coupon-group")
    public void listen(String message) throws JsonProcessingException {
        CouponEvent event = objectMapper.readValue(message, CouponEvent.class);

        switch (event.getEventType()) {
            case "coupon_created":
                handleCouponCreated(event);
                break;
            case "coupon_updated":
                handleCouponUpdated(event);
                break;
            case "coupon_assigned":
                handleCouponAssigned(event);
                break;
        }
    }

    private void handleCouponCreated(CouponEvent event) {
        // 쿠폰 생성 이벤트 처리 로직
    }

    private void handleCouponUpdated(CouponEvent event) {
        // 쿠폰 업데이트 이벤트 처리 로직
    }

    private void handleCouponAssigned(CouponEvent event) {
        // 쿠폰 할당 이벤트 처리 로직
    }
}
