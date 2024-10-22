package home.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LogService {
    private KafkaTemplate<String, String> kafkaTemplate;

    // 상품 조회 로그 발행
    public void sendProductViewLog(Long productId) {
        kafkaTemplate.send("product-view-log", "Product viewed: " + productId);
    }

    // 구매 활동 로그 발행
    public void sendPurchaseLog(Long orderId) {
        kafkaTemplate.send("purchase-activity-log", "Order created: " + orderId);
    }
}
