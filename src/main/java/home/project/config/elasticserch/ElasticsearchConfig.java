package home.project.config.elasticserch;

import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.lang.NonNull;

import javax.net.ssl.SSLContext;
import java.time.Duration;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.rest.username}") // 변경: rest 네임스페이스 사용
    private String username;

    @Value("${spring.elasticsearch.rest.password}") // 변경: rest 네임스페이스 사용
    private String password;

    @Value("${spring.elasticsearch.rest.uris}") // 변경: rest 네임스페이스 사용
    private String elasticsearchUrl;

    @Value("${spring.elasticsearch.rest.ssl.trust-store}") // 변경: rest 네임스페이스 사용
    private String trustStorePath;

    @Value("${spring.elasticsearch.rest.ssl.trust-store-password}") // 변경: rest 네임스페이스 사용
    private String trustStorePassword;

    @Value("${spring.elasticsearch.rest.ssl.key-store}") // 변경: rest 네임스페이스 사용
    private String keystorePath;

    @Value("${spring.elasticsearch.rest.ssl.key-store-password}") // 변경: rest 네임스페이스 사용
    private String keystorePassword;

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        try {
            // SSL Context 생성 (FileSystemResource 사용)
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(
                            new FileSystemResource(trustStorePath).getFile(), // FileSystemResource 사용
                            trustStorePassword.toCharArray() // trust-store-password 사용
                    )
                    .loadKeyMaterial(
                            new FileSystemResource(keystorePath).getFile(), // FileSystemResource 사용
                            keystorePassword.toCharArray(), // keystore-password 사용
                            keystorePassword.toCharArray()  // keystore-password 사용
                    )
                    .build();

            return ClientConfiguration.builder()
                    .connectedTo(elasticsearchUrl.replace("https://", ""))
                    .usingSsl(sslContext)
                    .withBasicAuth(username, password)
                    .withSocketTimeout(Duration.ofSeconds(30))
                    .withConnectTimeout(Duration.ofSeconds(60))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create SSL context for Elasticsearch", e);
        }
    }
}
