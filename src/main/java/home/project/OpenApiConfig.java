package home.project;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // 기본 응답 스키마 정의.
        Schema<?> baseResponseSchema = new ObjectSchema()
                .addProperty("result", new Schema<>().type("object"))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 페이지네이션된 목록 응답을 위한 일반적인 스키마.
        Schema<?> pagedListResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("totalCount", new Schema<>().type("integer").format("int64"))
                        .addProperty("page", new Schema<>().type("integer"))
                        .addProperty("content", new ArraySchema().items(new Schema<>().type("object"))))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 상품 추가 성공 응답 스키마.
        Schema<?> productCreateSuccessResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("successMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 상품 상세 스키마.
        Schema<?> productDetailSchema = new ObjectSchema()
                .addProperty("id", new Schema<>().type("integer").format("int64"))
                .addProperty("brand", new Schema<>().type("string"))
                .addProperty("soldQuantity", new Schema<>().type("integer").format("int64"))
                .addProperty("name", new Schema<>().type("string"))
                .addProperty("category", new Schema<>().type("string"))
                .addProperty("stock", new Schema<>().type("integer").format("int64"))
                .addProperty("image", new Schema<>().type("string"));

        // 상품 유효성 검사 실패 응답 스키마.
        Schema<?> productValidationFailedResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("brand", new Schema<>().type("string"))
                        .addProperty("category", new Schema<>().type("string"))
                        .addProperty("name", new Schema<>().type("string"))
                        .addProperty("stock", new Schema<>().type("string"))
                        .addProperty("image", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(400));

        Schema<?> stockChangeResponseSchema = new ObjectSchema()
                .addProperty("result", productDetailSchema)
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 로그인 성공 응답 스키마.
        Schema<?> loginSuccessResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("grantType", new Schema<>().type("string"))
                        .addProperty("accessToken", new Schema<>().type("string"))
                        .addProperty("refreshToken", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 로그인 실패 응답 스키마.
        Schema<?> loginFailedResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(401));

        // 로그아웃 성공 응답 스키마.
        Schema<?> logoutSuccessResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("successMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 권한 변경 성공 응답 스키마.
        Schema<?> authorityChangeSuccessResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64"))
                        .addProperty("role", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 회원가입 성공 응답 스키마.
        Schema<?> memberJoinSuccessResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("accessToken", new Schema<>().type("string"))
                        .addProperty("refreshToken", new Schema<>().type("string"))
                        .addProperty("successMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 회원 유효성 검사 실패 응답 스키마.
        Schema<?> memberValidationFailedResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("email", new Schema<>().type("string"))
                        .addProperty("password", new Schema<>().type("string"))
                        .addProperty("phone", new Schema<>().type("string"))
                        .addProperty("name", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(400));

        // 회원 정보 상세 스키마.
        Schema<?> memberDetailSchema = new ObjectSchema()
                .addProperty("id", new Schema<>().type("integer").format("int64"))
                .addProperty("email", new Schema<>().type("string"))
                .addProperty("name", new Schema<>().type("string"))
                .addProperty("phone", new Schema<>().type("string"));

        // Unauthorized 응답 스키마.
        Schema<?> unauthorizedResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(401));

        // Forbidden 응답 스키마.
        Schema<?> forbiddenResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(403));

        // Not Found 응답 스키마.
        Schema<?> notFoundResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(404));

        // Conflict 응답 스키마.
        Schema<?> conflictResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(409));

        return new OpenAPI()
                .info(new Info()
                        .title("예제 게시판 Swagger")
                        .version("1.0.0")
                        .description("API 문서입니다."))
                .components(new Components()
                        .addSchemas("BaseResponseSchema", baseResponseSchema)
                        .addSchemas("PagedListResponseSchema", pagedListResponseSchema)
                        .addSchemas("ProductCreateSuccessResponseSchema", productCreateSuccessResponseSchema)
                        .addSchemas("ProductResponseSchema", productDetailSchema)
                        .addSchemas("ProductValidationFailedResponseSchema", productValidationFailedResponseSchema)
                        .addSchemas("StockChangeResponseSchema", stockChangeResponseSchema)
                        .addSchemas("LoginSuccessResponseSchema", loginSuccessResponseSchema)
                        .addSchemas("LoginFailedResponseSchema", loginFailedResponseSchema)
                        .addSchemas("LogoutSuccessResponseSchema", logoutSuccessResponseSchema)
                        .addSchemas("AuthorityChangeSuccessResponseSchema", authorityChangeSuccessResponseSchema)
                        .addSchemas("MemberJoinSuccessResponseSchema", memberJoinSuccessResponseSchema)
                        .addSchemas("MemberValidationFailedResponseSchema", memberValidationFailedResponseSchema)
                        .addSchemas("MemberResponseSchema", memberDetailSchema)
                        .addSchemas("UnauthorizedResponseSchema", unauthorizedResponseSchema)
                        .addSchemas("ForbiddenResponseSchema", forbiddenResponseSchema)
                        .addSchemas("NotFoundResponseSchema", notFoundResponseSchema)
                        .addSchemas("ConflictResponseSchema", conflictResponseSchema));
    }
}