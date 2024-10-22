package home.project.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LogConsumerService {

    @KafkaListener(topics = "product-view-log", groupId = "log-consumers")
    public void consumeProductViewLog(String message) {
        System.out.println("Consumed product view log: " + message);
        sendLogToElk("product-view-log", message);
    }

    @KafkaListener(topics = "purchase-activity-log", groupId = "log-consumers")
    public void consumePurchaseLog(String message) {
        System.out.println("Consumed purchase log: " + message);
        sendLogToElk("purchase-activity-log", message);
    }

    // ELK로 로그 전송 로직 (HTTP 방식)
    private void sendLogToElk(String logType, String logMessage) {
        // Logstash나 ELK로 HTTP 전송을 통해 로그 전달
        System.out.println("Sending log to ELK: " + logType + " - " + logMessage);
    }
}
