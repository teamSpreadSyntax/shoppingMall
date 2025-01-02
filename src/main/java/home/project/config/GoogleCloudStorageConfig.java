package home.project.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GoogleCloudStorageConfig {

    @Bean
    public Storage storage() throws IOException {
        // 서비스 계정 키 파일에서 인증 정보 로드
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new FileInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS")));

        // Storage 객체 생성 및 반환
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }
}