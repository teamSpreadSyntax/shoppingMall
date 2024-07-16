package home.project;


import home.project.domain.CustomOptionalResponseBody;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@EnableWebSecurity
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        // Result schema for the successful response
        Schema<?> resultSchema200 = new ObjectSchema()
                .addProperty("accessToken", new Schema<>().type("string"))
                .addProperty("refreshToken", new Schema<>().type("string"))
                .addProperty("successMessage", new Schema<>().type("string"));

        // Result schema for the bad request response
        Schema<?> resultSchema400 = new ObjectSchema()
                .addProperty("email", new Schema<>().type("string"));

        // Result schema for the conflict response
        Schema<?> resultSchema409 = new ObjectSchema()
                .addProperty("errorMessage", new Schema<>().type("string"));

        // Result schema for the internal server error response
        Schema<?> resultSchema500 = new ObjectSchema()
                .addProperty("errorMessage", new Schema<>().type("string"));

        // Common response body schema
        Schema<?> customOptionalResponseBodySchema200 = new ObjectSchema()
                .addProperty("result", resultSchema200)
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> customOptionalResponseBodySchema400 = new ObjectSchema()
                .addProperty("result", resultSchema400)
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(400));

        Schema<?> customOptionalResponseBodySchema409 = new ObjectSchema()
                .addProperty("result", resultSchema409)
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(409));

        Schema<?> customOptionalResponseBodySchema500 = new ObjectSchema()
                .addProperty("result", resultSchema500)
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(500));

        return new OpenAPI()
                .info(new Info()
                        .title("예제 게시판 Swagger")
                        .version("1.0.0")
                        .description("API 문서입니다."))
                .components(new Components()
                        .addSchemas("CustomOptionalResponseBody200", customOptionalResponseBodySchema200)
                        .addSchemas("CustomOptionalResponseBody400", customOptionalResponseBodySchema400)
                        .addSchemas("CustomOptionalResponseBody409", customOptionalResponseBodySchema409)
                        .addSchemas("CustomOptionalResponseBody500", customOptionalResponseBodySchema500));
    }

}


