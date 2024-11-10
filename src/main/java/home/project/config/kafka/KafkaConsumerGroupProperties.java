package home.project.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.kafka.consumer.groups")
public class KafkaConsumerGroupProperties {
    private String kafka = "elk-logstash";
    private String error = "elk-logstash";
}