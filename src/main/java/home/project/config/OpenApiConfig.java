package home.project.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        // 로그인 성공 응답 스키마
        Schema<?> loginSuccessResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("grantType", new Schema<>().type("string"))
                        .addProperty("accessToken", new Schema<>().type("string"))
                        .addProperty("refreshToken", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 로그인 유효성 검사 실패 응답 스키마
        Schema<?> loginValidationFailedResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("password", new Schema<>().type("string"))
                        .addProperty("email", new Schema<>().type("string"))
                )
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(400));

        // 사용자 권한 스키마
        Schema<?> userRoleSchema = new ObjectSchema()
                .addProperty("id", new Schema<>().type("integer").format("int64"))
                .addProperty("role", new Schema<>().type("string"))
                .addProperty("name", new Schema<>().type("string"));

        // 사용자별 권한 목록 응답 스키마
        Schema<?> pagedUserRoleListResponseSchema = new ObjectSchema()
            .addProperty("result", new ObjectSchema()
                .addProperty("totalCount", new Schema<>().type("integer").format("int64"))
                .addProperty("page", new Schema<>().type("integer"))
                .addProperty("content", new ArraySchema().items(userRoleSchema)
                        .addProperty("id", new Schema<>().type("integer").format("int64"))
                        .addProperty("role", new Schema<>().type("string"))
                        .addProperty("name", new Schema<>().type("string"))))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 권한 변경 성공 응답 스키마
        Schema<?> authorityChangeSuccessResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64"))
                        .addProperty("role", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 기본 응답 스키마 정의.
        Schema<?> baseResponseSchema = new ObjectSchema()
                .addProperty("result", new Schema<>().type("object"))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 일반 성공 응답 스키마
        Schema<?> generalSuccessResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("successMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 상품 스키마
        Schema<?> productSchema = new ObjectSchema()
                .addProperty("id", new Schema<>().type("integer").format("int64"))
                .addProperty("name", new Schema<>().type("string"))
                .addProperty("brand", new Schema<>().type("string"))
                .addProperty("category", new Schema<>().type("string"))
                .addProperty("soldQuantity", new Schema<>().type("integer").format("int64"))
                .addProperty("stock", new Schema<>().type("integer").format("int64"))
                .addProperty("image", new Schema<>().type("string"));

        // 상품 응답 스키마
        Schema<?> productResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64"))
                        .addProperty("name", new Schema<>().type("string"))
                        .addProperty("brand", new Schema<>().type("string"))
                        .addProperty("category", new Schema<>().type("string"))
                        .addProperty("soldQuantity", new Schema<>().type("integer").format("int64"))
                        .addProperty("stock", new Schema<>().type("integer").format("int64"))
                        .addProperty("image", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        //브랜드 목록 스키마
        Schema<?> brandListResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("totalCount", new Schema<>().type("integer"))
                        .addProperty("page", new Schema<>().type("integer"))
                        .addProperty("content", new ArraySchema().items(new Schema<>().type("string")))
                )
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 페이지네이션된 상품 목록 스키마
        Schema<?> pagedProductListResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("totalCount", new Schema<>().type("integer").format("int64"))
                        .addProperty("page", new Schema<>().type("integer"))
                        .addProperty("content", new ArraySchema().items(productSchema)))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 상품 유효성 검사 실패 응답 스키마
        Schema<?> productValidationFailedResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("name", new Schema<>().type("string"))
                        .addProperty("brand", new Schema<>().type("string"))
                        .addProperty("category", new Schema<>().type("string"))
                        .addProperty("image", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(400));

        // 회원 스키마
        Schema<?> memberWithoutPwSchema = new ObjectSchema()
                .addProperty("id", new Schema<>().type("integer").format("int64"))
                .addProperty("email", new Schema<>().type("string"))
                .addProperty("name", new Schema<>().type("string"))
                .addProperty("phone", new Schema<>().type("string"))
                .addProperty("role", new Schema<>().type("string"));

        // 회원 응답 스키마
        Schema<?> memberResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64"))
                        .addProperty("email", new Schema<>().type("string"))
                        .addProperty("name", new Schema<>().type("string"))
                        .addProperty("password", new Schema<>().type("string"))
                        .addProperty("phone", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 회원 응답 스키마
        Schema<?> memberWithoutPasswordResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64"))
                        .addProperty("email", new Schema<>().type("string"))
                        .addProperty("name", new Schema<>().type("string"))
                        .addProperty("phone", new Schema<>().type("string"))
                        .addProperty("role", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 페이지네이션된 회원 목록 스키마
        Schema<?> pagedMemberListResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("totalCount", new Schema<>().type("integer").format("int64"))
                        .addProperty("page", new Schema<>().type("integer"))
                        .addProperty("content", new ArraySchema().items(memberWithoutPwSchema)))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 회원가입 성공 응답 스키마
        Schema<?> memberJoinSuccessResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("accessToken", new Schema<>().type("string"))
                        .addProperty("refreshToken", new Schema<>().type("string"))
                        .addProperty("successMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        // 회원 유효성 검사 실패 응답 스키마
        Schema<?> memberValidationFailedResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("email", new Schema<>().type("string"))
                        .addProperty("password", new Schema<>().type("string"))
                        .addProperty("passwordConfirm", new Schema<>().type("string"))
                        .addProperty("phone", new Schema<>().type("string"))
                        .addProperty("name", new Schema<>().type("string"))
                )
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(400));

        // Unauthorized 응답 스키마
        Schema<?> badRequestResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(400));

        // Unauthorized 응답 스키마
        Schema<?> unauthorizedResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(401));

        // Forbidden 응답 스키마
        Schema<?> forbiddenResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(403));

        // Not Found 응답 스키마
        Schema<?> notFoundResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(404));

        // Conflict 응답 스키마
        Schema<?> conflictResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(409));

        // Internal Server Error 응답 스키마
        Schema<?> internalServerErrorResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("errorMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(500));

        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Token");

        return new OpenAPI()
                .info(new Info()
                        .title("예제 게시판 Swagger")
                        .version("1.0.0")
                        .description("API 문서입니다."))
                .components(new Components()
                        .addSchemas("BaseResponseSchema", baseResponseSchema)
                        .addSchemas("GeneralSuccessResponseSchema", generalSuccessResponseSchema)
                        .addSchemas("ProductSchema", productSchema)
                        .addSchemas("ProductResponseSchema", productResponseSchema)
                        .addSchemas("PagedProductListResponseSchema", pagedProductListResponseSchema)
                        .addSchemas("BrandListResponseSchema", brandListResponseSchema)
                        .addSchemas("ProductValidationFailedResponseSchema", productValidationFailedResponseSchema)
                        .addSchemas("LoginSuccessResponseSchema", loginSuccessResponseSchema)
                        .addSchemas("PagedUserRoleListResponseSchema", pagedUserRoleListResponseSchema)
                        .addSchemas("AuthorityChangeSuccessResponseSchema", authorityChangeSuccessResponseSchema)
                        .addSchemas("LoginValidationFailedResponseSchema", loginValidationFailedResponseSchema)
                        .addSchemas("MemberWithoutPwSchema", memberWithoutPwSchema)
                        .addSchemas("MemberResponseSchema", memberResponseSchema)
                        .addSchemas("MemberWithoutPasswordResponseSchema", memberWithoutPasswordResponseSchema)
                        .addSchemas("PagedMemberListResponseSchema", pagedMemberListResponseSchema)
                        .addSchemas("MemberJoinSuccessResponseSchema", memberJoinSuccessResponseSchema)
                        .addSchemas("MemberValidationFailedResponseSchema", memberValidationFailedResponseSchema)
                        .addSchemas("BadRequestResponseSchema", badRequestResponseSchema)
                        .addSchemas("UnauthorizedResponseSchema", unauthorizedResponseSchema)
                        .addSchemas("ForbiddenResponseSchema", forbiddenResponseSchema)
                        .addSchemas("NotFoundResponseSchema", notFoundResponseSchema)
                        .addSchemas("ConflictResponseSchema", conflictResponseSchema)
                        .addSchemas("InternalServerErrorResponseSchema", internalServerErrorResponseSchema)
                        .addSecuritySchemes("bearerAuth", apiKey))
                .addSecurityItem(securityRequirement);
    }
}