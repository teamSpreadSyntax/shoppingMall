package home.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.dto.CouponEventDTO;
import home.project.dto.MemberEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendCouponEvent(CouponEventDTO event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("coupon-events", message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendMemberJoinEvent(MemberEventDTO memberEventDTO) {
        try {
            String message = objectMapper.writeValueAsString(memberEventDTO);
            kafkaTemplate.send("member-join-events", message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendProductViewLog(Long productId) {
        kafkaTemplate.send("product-view-log", "Product viewed: " + productId);
    }

    public void sendPurchaseLog(Long orderId) {
        kafkaTemplate.send("purchase-activity-log", "Order created: " + orderId);
    }
}