package home.project.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaLogConsumer {

    private final LogProcessingService logProcessingService;

    @KafkaListener(topics = "kafka-logs", groupId = "elk-logstash")
    public void consumeApplicationLog(String message) {
        try {
            logProcessingService.processLog("kafka-logs", message);
        } catch (Exception e) {
            log.error("Error processing application log", e);
        }
    }

    @KafkaListener(topics = "error-logs", groupId = "elk-logstash")
    public void consumeErrorLog(String message) {
        try {
            logProcessingService.processLog("error-logs", message);
        } catch (Exception e) {
            log.error("Error processing error log", e);
        }
    }
}
