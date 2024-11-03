package home.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.dto.CouponEventDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaEventListenerService {

    private final ObjectMapper objectMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    private final ElkLogSenderService elkLogSenderService;

    @KafkaListener(topics = "coupon-events", groupId = "coupon-group")
    public void listenCouponEvents(String message) throws JsonProcessingException {
        CouponEventDTO event = objectMapper.readValue(message, CouponEventDTO.class);
        switch (event.getEventType()) {
            case "coupon_assigned_to_member":
                handleCouponAssignedToMember(event);
                break;
            case "coupon_assigned_to_product":
                handleCouponAssignedToProduct(event);
                break;
        }
        elkLogSenderService.sendLogToElk("coupon-events", message);
    }

    @KafkaListener(topics = "product-view-log", groupId = "product-group")
    public void listenProductEvents(String message) {
        System.out.println("Product viewed log: " + message);
        elkLogSenderService.sendLogToElk("product-view-log", message);
    }

    @KafkaListener(topics = "member-join-events", groupId = "member-group")
    public void listenMemberEvents(String message) {
        System.out.println("member-join: " + message);
        elkLogSenderService.sendLogToElk("member-join-events", message);
    }

    @KafkaListener(topics = "orders-events", groupId = "orders-group")
    public void listenOrdersEvents(String message) {
        System.out.println("orders-events: " + message);
        elkLogSenderService.sendLogToElk("orders-events", message);
    }

    private void handleCouponAssignedToMember(CouponEventDTO couponEventDTO) {
        // 쿠폰을 회원에게 할당하는 처리 로직
        webSocketNotificationService.sendNotification(
                "/queue/notifications",
                "New coupon assigned to member: " + couponEventDTO.getCouponId()
        );
    }

    private void handleCouponAssignedToProduct(CouponEventDTO couponEventDTO) {
        // 쿠폰을 상품에 할당하는 처리 로직
        webSocketNotificationService.sendNotification(
                "/queue/notifications",
                "New coupon assigned to product: " + couponEventDTO.getCouponId()
        );
    }
}
