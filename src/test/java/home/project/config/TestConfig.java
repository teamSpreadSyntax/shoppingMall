package home.project.config;

import home.project.service.file.FileService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.mock;

@TestConfiguration
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class TestConfig {

    @Bean
    public FileService fileService() {
        return new FileService(null) { // Storage 객체 필요 없음
            @Override
            public String saveFile(MultipartFile file, String domain, String userId) {
                return "mock-file-url"; // 항상 동일한 URL 반환
            }
        };
    }
}

