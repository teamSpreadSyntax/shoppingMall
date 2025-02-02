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
        String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new RuntimeException("GOOGLE_APPLICATION_CREDENTIALS 환경 변수가 설정되지 않았습니다.");
        }

        // 환경 변수에서 읽어서 인증
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));

        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

}