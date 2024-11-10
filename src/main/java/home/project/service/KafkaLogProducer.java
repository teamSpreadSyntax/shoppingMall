/*
package home.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.dto.kafkaDTO.CouponEventDTO;
import home.project.dto.kafkaDTO.MemberEventDTO;
import home.project.dto.kafkaDTO.OrderEventDTO;
import home.project.logging.LogFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaLogProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String LOG_TOPIC = "elk-logstash";
    private static final String ERROR_LOG_TOPIC = "elk-logstash";
    private static final String SLOW_API_TOPIC = "elk-logstash";

    public void sendLog(LogFormat logFormat) {
        try {
            String message = objectMapper.writeValueAsString(logFormat);

            // 일반 로그
            kafkaTemplate.send(LOG_TOPIC, logFormat.getTraceId(), message);

            // 에러 로그
            if (logFormat.getErrorMessage() != null) {
                kafkaTemplate.send(ERROR_LOG_TOPIC, logFormat.getTraceId(), message);
            }

            // 느린 API 로그
            if (logFormat.getElapsedTime() > 1000) {
                kafkaTemplate.send(SLOW_API_TOPIC, logFormat.getTraceId(), message);
            }
        } catch (Exception e) {
            log.error("Failed to send log to Kafka", e);
        }
    }
}
*/
