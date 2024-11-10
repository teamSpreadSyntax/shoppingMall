package home.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.dto.kafkaDTO.CouponEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaLogConsumer {
    private final ObjectMapper objectMapper;
    private final ElkLogSenderService elkLogSenderService;  // ElasticSearch로 로그 전송용

    @KafkaListener(topics = "kafka-logs", groupId = "elk-logstash")
    public void consumeApplicationLog(String message) {
        try {
            elkLogSenderService.sendLogToElk("kafka-logs", message);
            log.info("Application log received: {}", message);
        } catch (Exception e) {
            log.error("Error processing application log", e);
        }
    }

    @KafkaListener(topics = "error-logs", groupId = "elk-logstash")
    public void consumeErrorLog(String message) {
        try {
            elkLogSenderService.sendLogToElk("error-logs", message);
            log.error("Error log received: {}", message);
        } catch (Exception e) {
            log.error("Error processing error log", e);
        }
    }

    /*@KafkaListener(topics = "slow-api-logs", groupId = "elk-logstash")
    public void consumeSlowApiLog(String message) {
        try {
            elkLogSenderService.sendLogToElk("slow-api-logs", message);
            log.warn("Slow API log received: {}", message);
        } catch (Exception e) {
            log.error("Error processing slow API log", e);
        }
    }*/
}
