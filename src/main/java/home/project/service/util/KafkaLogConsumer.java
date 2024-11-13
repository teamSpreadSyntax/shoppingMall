package home.project.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaLogConsumer {
    private final ObjectMapper objectMapper;
    private final ElkLogSenderService elkLogSenderService;

    @KafkaListener(topics = "kafka-logs", groupId = "elk-logstash")
    public void consumeApplicationLog(String message) {
        try {
            // 로그 순환을 방지하기 위해 이미 처리된 로그인지 확인
            if (isProcessedLog(message)) {
                return;
            }

            elkLogSenderService.sendLogToElk("kafka-logs", message);

            // 디버그 레벨로 변경하거나 필요한 경우에만 로깅
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

            // 에러 로그는 중요하므로 유지하되 메시지 전체가 아닌 핵심 정보만 로깅
            log.error("Processed error log");
        } catch (Exception e) {
            log.error("Error processing error log", e);
        }
    }

    // 이미 처리된 로그인지 확인하는 메서드
    private boolean isProcessedLog(String message) {
        return message.contains("Application log received") ||
                message.contains("Error log received") ||
                message.contains("Processed application log") ||
                message.contains("Processed error log");
    }
}
