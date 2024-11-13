package home.project.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic kafkaLogsTopic() {  // application logs용 토픽
        return TopicBuilder.name("kafka-logs")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic errorLogsTopic() {  // error logs용 토픽
        return TopicBuilder.name("error-logs")
                .partitions(1)
                .replicas(1)
                .build();
    }
}