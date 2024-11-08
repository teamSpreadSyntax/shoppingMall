package home.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.dto.kafkaDTO.CouponEventDTO;
import home.project.dto.kafkaDTO.MemberEventDTO;
import home.project.dto.kafkaDTO.OrderEventDTO;
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
            //CouponEventDTO 객체를 받아서 json 으로 변환후 message 변수에 저장
            kafkaTemplate.send("coupon-events", message);
            //coupon-events 라는 토픽 상자를 만들고 message 내용을 그안에 넣는다.
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

    public void sendOrderEvent(OrderEventDTO orderEventDTO) {
        try {
            String message = objectMapper.writeValueAsString(orderEventDTO);
            kafkaTemplate.send("orders-events", message);
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