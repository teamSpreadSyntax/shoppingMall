package home.project.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Configuration
@PropertySource("classpath:security-permissions.properties")
@ConfigurationProperties(prefix = "security")
public class SecurityPermissionsConfig {
    private List<String> permitAll = new ArrayList<>();
    private Map<String, List<String>> hasAnyRole = new HashMap<>();
    private Map<String, List<String>> hasRole = new HashMap<>();

}