package home.project;


import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@EnableWebSecurity
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("예제 게시판 Swagger")
                .version("1.0.0")
                .description("API 문서입니다."));
    }

}