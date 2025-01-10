package home.project.config;

import home.project.service.util.FileService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@TestConfiguration
public class TestConfig {

    @Bean
    public FileService fileService() {
        return new FileService(null) { // Storage를 null로 설정
            @Override
            public String saveFile(MultipartFile file, String domain, String userId) {
                return String.format("https://test.storage.googleapis.com/%s/%s/%s/%s",
                        domain, userId, LocalDate.now(), UUID.randomUUID().toString() + ".txt");
            }
        };
    }
}

