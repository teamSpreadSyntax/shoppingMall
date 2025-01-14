package home.project.service.integration;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ElkLogSenderService {

    public void sendLogToElk(String logType, String logMessage) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String logstashUrl = "http://logstash:5000"; // Logstash HTTP 입력 URL
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