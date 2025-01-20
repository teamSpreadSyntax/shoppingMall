package home.project.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.Map;

@Configuration
@EnableWebSecurity
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Schema<?> tokenResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                    .addProperty("accessToken", new Schema<>().type("string"))
                    .addProperty("refreshToken", new Schema<>().type("string"))
                    .addProperty("role", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));


        // 단일 응답을 위한 기본 스키마
        Schema<?> baseResponseSchema = new ObjectSchema()
                .addProperty("result", new Schema<>().type("object"))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 리스트 응답을 위한 기본 스키마
        Schema<?> baseListResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("totalCount", new Schema<>().type("integer").format("int64"))
                        .addProperty("page", new Schema<>().type("integer"))
                        .addProperty("content", new ArraySchema().items(new Schema<>().type("array"))))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 상품 응답 스키마 예시
        Schema<?> productResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                                .addProperty("id", new Schema<>().type("integer").format("int64"))
                                .addProperty("name", new Schema<>().type("string"))
                                .addProperty("brand", new Schema<>().type("string"))
                )
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));


        // 에러 응답 스키마들
        Map<String, Schema> errorSchemas = Map.of(
                "BadRequest", createErrorSchema(400),
                "Unauthorized", createErrorSchema(401),
                "Forbidden", createErrorSchema(403),
                "NotFound", createErrorSchema(404),
                "Conflict", createErrorSchema(409),
                "InternalServer", createErrorSchema(500)
        );

        // SecurityScheme 설정
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Components에 스키마 추가
        Components components = new Components()
                .addSecuritySchemes("bearerAuth", bearerAuth)
                .addSchemas("BaseResponse", baseResponseSchema)
                .addSchemas("BaseListResponse", baseListResponseSchema)
                .addSchemas("ProductResponse", productResponseSchema)
//                .addSchemas("PagedProductList", pagedProductListResponseSchema)
                .addSchemas("TokenResponse", tokenResponseSchema);

        // 에러 스키마들 추가
        errorSchemas.forEach(components::addSchemas);

        return new OpenAPI()
                .info(new Info()
                        .title("API 문서")
                        .version("1.0.0")
                        .description("API 문서입니다."))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private Schema<?> createErrorSchema(int statusCode) {
        return new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(statusCode));
    }
}