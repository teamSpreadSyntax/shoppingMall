package home.project.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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
        // 실제로 Logstash에 HTTP 요청을 통해 로그를 전달하는 코드 추가
        try {
            RestTemplate restTemplate = new RestTemplate();
            String logstashUrl = "http://logstash:5000"; // Logstash의 HTTP 입력 URL 설정
            Map<String, String> logData = new HashMap<>();
            logData.put("logType", logType);
            logData.put("logMessage", logMessage);

            restTemplate.postForEntity(logstashUrl, logData, String.class);
            System.out.println("Sent log to ELK: " + logType + " - " + logMessage);
        } catch (Exception e) {
            System.err.println("Failed to send log to ELK: " + e.getMessage());
        }
    }
}
