package home.project.service.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaLogConsumer {
    private final ElkLogSenderService elkLogSenderService;

    @KafkaListener(topics = "kafka-logs", groupId = "elk-logstash")
    public void consumeApplicationLog(String message) {
        try {
            if (isProcessedLog(message)) {
                return;
            }

            elkLogSenderService.sendLogToElk("kafka-logs", message);

            if (log.isDebugEnabled()) {
                log.debug("Processed application log");
            }
        } catch (Exception e) {
            log.error("Error processing application log", e);
        }
    }

    @KafkaListener(topics = "error-logs", groupId = "elk-logstash")
    public void consumeErrorLog(String message) {
        try {
            if (isProcessedLog(message)) {
                return;
            }

            elkLogSenderService.sendLogToElk("error-logs", message);

            log.error("Processed error log");
        } catch (Exception e) {
            log.error("Error processing error log", e);
        }
    }

    private boolean isProcessedLog(String message) {
        return message.contains("Application log received") ||
                message.contains("Error log received") ||
                message.contains("Processed application log") ||
                message.contains("Processed error log");
    }
}
