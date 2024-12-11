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
import org.springframework.data.domain.Page;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        Schema<?> cartResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("email", new Schema<>().type("string").example("user@example.com"))
                        .addProperty("products", new ArraySchema().items(new ObjectSchema()
                                .addProperty("productId", new Schema<>().type("integer").example(1))
                                .addProperty("quantity", new Schema<>().type("integer").example(2)))))
                .addProperty("responseMessage", new Schema<>().type("string").example("장바구니 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> categoryResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("code", new Schema<>().type("string").example("0001"))
                        .addProperty("name", new Schema<>().type("string").example("패션"))
                        .addProperty("level", new Schema<>().type("integer").example(1))
                        .addProperty("parent", new ObjectSchema().nullable(true))
                        .addProperty("children", new ArraySchema().items(new ObjectSchema()
                                .addProperty("id", new Schema<>().type("integer").format("int64").example(2))
                                .addProperty("name", new Schema<>().type("string").example("아웃도어")))))
                .addProperty("responseMessage", new Schema<>().type("string").example("카테고리 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> couponResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("name", new Schema<>().type("string").example("할인 쿠폰"))
                        .addProperty("discountRate", new Schema<>().type("integer").example(10))
                        .addProperty("startDate", new Schema<>().type("string").format("date-time").example("2024-01-01T00:00:00Z"))
                        .addProperty("endDate", new Schema<>().type("string").format("date-time").example("2024-12-31T23:59:59Z")))
                .addProperty("responseMessage", new Schema<>().type("string").example("쿠폰 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> eventResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("name", new Schema<>().type("string").example("새해 맞이 세일"))
                        .addProperty("description", new Schema<>().type("string").example("새해를 맞아 최대 50% 세일"))
                        .addProperty("startDate", new Schema<>().type("string").format("date-time").example("2024-01-01T00:00:00Z"))
                        .addProperty("endDate", new Schema<>().type("string").format("date-time").example("2024-01-31T23:59:59Z"))
                        .addProperty("image", new Schema<>().type("string").example("https://example.com/image.jpg")))
                .addProperty("responseMessage", new Schema<>().type("string").example("이벤트 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> memberCouponResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("memberEmail", new Schema<>().type("string").example("user@example.com"))
                        .addProperty("couponId", new Schema<>().type("integer").format("int64").example(101))
                        .addProperty("discountRate", new Schema<>().type("integer").example(15))
                        .addProperty("issuedAt", new Schema<>().type("string").format("date-time").example("2024-01-01T00:00:00Z"))
                        .addProperty("usedAt", new Schema<>().type("string").format("date-time").example("2024-01-15T00:00:00Z"))
                        .addProperty("isUsed", new Schema<>().type("boolean").example(false)))
                .addProperty("responseMessage", new Schema<>().type("string").example("회원 쿠폰 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> memberEventResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("memberEmail", new Schema<>().type("string").example("user@example.com"))
                        .addProperty("eventId", new Schema<>().type("integer").format("int64").example(202))
                        .addProperty("createdAt", new Schema<>().type("string").format("date-time").example("2024-01-01T12:00:00Z")))
                .addProperty("responseMessage", new Schema<>().type("string").example("회원 이벤트 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> memberResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("email", new Schema<>().type("string").example("user@example.com"))
                        .addProperty("name", new Schema<>().type("string").example("홍길동"))
                        .addProperty("phone", new Schema<>().type("string").example("01012345678"))
                        .addProperty("role", new Schema<>().type("string").example("ADMIN"))
                        .addProperty("gender", new Schema<>().type("string").example("F"))
                        .addProperty("birthDate", new Schema<>().type("string").format("date").example("1990-01-01"))
                        .addProperty("defaultAddress", new Schema<>().type("string").example("서울특별시 강남구 테헤란로"))
                        .addProperty("secondAddress", new Schema<>().type("string").example("서울특별시 서초구 서초대로"))
                        .addProperty("thirdAddress", new Schema<>().type("string").example("서울특별시 송파구 올림픽로"))
                        .addProperty("grade", new Schema<>().type("string").example("GOLD"))
                        .addProperty("point", new Schema<>().type("integer").example(1000))
                        .addProperty("memberCouponResponse", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/MemberCouponResponse"))))
                .addProperty("responseMessage", new Schema<>().type("string").example("회원 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> memberResponseForUserSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("email", new Schema<>().type("string").example("user@example.com"))
                        .addProperty("name", new Schema<>().type("string").example("홍길동"))
                        .addProperty("phone", new Schema<>().type("string").example("01012345678"))
                        .addProperty("gender", new Schema<>().type("string").example("MALE"))
                        .addProperty("birthDate", new Schema<>().type("string").format("date").example("1990-01-01"))
                        .addProperty("defaultAddress", new Schema<>().type("string").example("서울특별시 강남구 테헤란로"))
                        .addProperty("secondAddress", new Schema<>().type("string").example("서울특별시 서초구 서초대로"))
                        .addProperty("thirdAddress", new Schema<>().type("string").example("서울특별시 송파구 올림픽로"))
                        .addProperty("grade", new Schema<>().type("string").example("GOLD"))
                        .addProperty("point", new Schema<>().type("integer").example(1000))
                        .addProperty("memberCouponResponse", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/MemberCouponResponse"))))
                .addProperty("responseMessage", new Schema<>().type("string").example("회원 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> myCartResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("products", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/ProductDTOForOrder"))))
                .addProperty("responseMessage", new Schema<>().type("string").example("내 장바구니 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> notificationDetailResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("notificationType", new Schema<>().type("string").example("ORDER_COMPLETE"))
                        .addProperty("description", new Schema<>().type("string").example("주문이 완료되었습니다."))
                        .addProperty("createdAt", new Schema<>().type("string").format("date-time").example("2024-01-01T12:00:00Z")))
                .addProperty("responseMessage", new Schema<>().type("string").example("알림 세부 정보 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> notificationResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("memberId", new Schema<>().type("integer").format("int64").example(101))
                        .addProperty("notificationType", new Schema<>().type("string").example("ORDER_COMPLETE"))
                        .addProperty("description", new Schema<>().type("string").example("주문이 완료되었습니다."))
                        .addProperty("isRead", new Schema<>().type("boolean").example(false))
                        .addProperty("createdAt", new Schema<>().type("string").format("date-time").example("2024-01-01T12:00:00Z")))
                .addProperty("responseMessage", new Schema<>().type("string").example("알림 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> orderResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("orderNum", new Schema<>().type("string").example("ORD-2024-0001"))
                        .addProperty("orderDate", new Schema<>().type("string").format("date-time").example("2024-01-01T10:00:00Z"))
                        .addProperty("deliveryAddress", new Schema<>().type("string").example("서울특별시 강남구 테헤란로"))
                        .addProperty("totalAmount", new Schema<>().type("integer").format("int64").example(50000))
                        .addProperty("pointsUsed", new Schema<>().type("integer").format("int64").example(500))
                        .addProperty("pointsEarned", new Schema<>().type("integer").format("int64").example(100))
                        .addProperty("products", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/ProductDTOForOrder"))))
                .addProperty("responseMessage", new Schema<>().type("string").example("주문 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> productCouponResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("productNum", new Schema<>().type("string").example("PROD-2024-001"))
                        .addProperty("couponId", new Schema<>().type("integer").format("int64").example(101))
                        .addProperty("issuedAt", new Schema<>().type("string").format("date-time").example("2024-01-01T12:00:00Z"))
                        .addProperty("usedAt", new Schema<>().type("string").format("date-time").example("2024-01-15T12:00:00Z"))
                        .addProperty("discountRate", new Schema<>().type("integer").example(20))
                        .addProperty("isUsed", new Schema<>().type("boolean").example(false)))
                .addProperty("responseMessage", new Schema<>().type("string").example("상품 쿠폰 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> productEventResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("productNum", new Schema<>().type("string").example("PROD-2024-001"))
                        .addProperty("eventId", new Schema<>().type("integer").format("int64").example(101))
                        .addProperty("createdAt", new Schema<>().type("string").format("date-time").example("2024-01-01T12:00:00Z")))
                .addProperty("responseMessage", new Schema<>().type("string").example("상품 이벤트 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> productResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("name", new Schema<>().type("string").example("블루 데님 자켓"))
                        .addProperty("brand", new Schema<>().type("string").example("리바이스"))
                        .addProperty("category", new Schema<>().type("string").example("아우터/자켓"))
                        .addProperty("productNum", new Schema<>().type("string").example("PROD-2024-001"))
                        .addProperty("price", new Schema<>().type("integer").format("int64").example(89000))
                        .addProperty("discountRate", new Schema<>().type("integer").example(20))
                        .addProperty("description", new Schema<>().type("string"))
                        .addProperty("imageUrl", new Schema<>().type("string").example("https://example.com/image.jpg"))
                        .addProperty("isLiked", new Schema<>().type("boolean").example(true))
                        .addProperty("size", new Schema<>().type("string").example("M"))
                        .addProperty("color", new Schema<>().type("string").example("블루"))
                        .addProperty("productCouponResponse", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/ProductCouponResponse"))))
                .addProperty("responseMessage", new Schema<>().type("string").example("상품 상세 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> productResponseForManagerSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("name", new Schema<>().type("string").example("블루 데님 자켓"))
                        .addProperty("brand", new Schema<>().type("string").example("리바이스"))
                        .addProperty("category", new Schema<>().type("string").example("아우터/자켓"))
                        .addProperty("productNum", new Schema<>().type("string").example("PROD-2024-001"))
                        .addProperty("stock", new Schema<>().type("integer").format("int64").example(100))
                        .addProperty("soldQuantity", new Schema<>().type("integer").format("int64").example(50))
                        .addProperty("price", new Schema<>().type("integer").format("int64").example(89000))
                        .addProperty("discountRate", new Schema<>().type("integer").example(20))
                        .addProperty("defectiveStock", new Schema<>().type("integer").format("int64").example(2))
                        .addProperty("description", new Schema<>().type("string"))
                        .addProperty("createProductDate", new Schema<>().type("string").format("date-time").example("2024-01-01T10:00:00Z"))
                        .addProperty("imageUrl", new Schema<>().type("string").example("https://example.com/image.jpg"))
                        .addProperty("size", new Schema<>().type("string").example("M"))
                        .addProperty("color", new Schema<>().type("string").example("블루"))
                        .addProperty("productCouponResponse", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/ProductCouponResponse"))))
                .addProperty("responseMessage", new Schema<>().type("string").example("관리자용 상품 상세 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> productWithQnAAndReviewResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("name", new Schema<>().type("string").example("블루 데님 자켓"))
                        .addProperty("brand", new Schema<>().type("string").example("리바이스"))
                        .addProperty("category", new Schema<>().type("string").example("아우터/자켓"))
                        .addProperty("productNum", new Schema<>().type("string").example("PROD-2024-001"))
                        .addProperty("price", new Schema<>().type("integer").format("int64").example(89000))
                        .addProperty("discountRate", new Schema<>().type("integer").example(20))
                        .addProperty("description", new Schema<>().type("string"))
                        .addProperty("imageUrl", new Schema<>().type("string").example("https://example.com/image.jpg"))
                        .addProperty("isLiked", new Schema<>().type("boolean").example(true))
                        .addProperty("size", new Schema<>().type("string").example("M"))
                        .addProperty("color", new Schema<>().type("string").example("블루"))
                        .addProperty("productCouponResponses", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/ProductCouponResponse")))
                        .addProperty("qnADetailResponses", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/QnADetailResponse")))
                        .addProperty("reviewDetailResponses", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/ReviewDetailResponse"))))
                .addProperty("responseMessage", new Schema<>().type("string").example("상품 상세 및 리뷰 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> productSimpleResponseForManagerSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("name", new Schema<>().type("string").example("블루 데님 자켓"))
                        .addProperty("brand", new Schema<>().type("string").example("리바이스"))
                        .addProperty("stock", new Schema<>().type("integer").format("int64").example(100))
                        .addProperty("soldQuantity", new Schema<>().type("integer").format("int64").example(50))
                        .addProperty("price", new Schema<>().type("integer").format("int64").example(89000))
                        .addProperty("discountRate", new Schema<>().type("integer").example(20))
                        .addProperty("createProductDate", new Schema<>().type("string").format("date-time").example("2024-01-01T10:00:00Z"))
                        .addProperty("size", new Schema<>().type("string").example("M"))
                        .addProperty("color", new Schema<>().type("string").example("블루")))
                .addProperty("responseMessage", new Schema<>().type("string").example("관리자용 상품 간단 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> productSimpleResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("name", new Schema<>().type("string").example("블루 데님 자켓"))
                        .addProperty("brand", new Schema<>().type("string").example("리바이스"))
                        .addProperty("price", new Schema<>().type("integer").format("int64").example(89000))
                        .addProperty("discountRate", new Schema<>().type("integer").example(20))
                        .addProperty("imageUrl", new Schema<>().type("string").example("https://example.com/image.jpg"))
                        .addProperty("isLiked", new Schema<>().type("boolean").example(true))
                        .addProperty("color", new Schema<>().type("string").example("블루")))
                .addProperty("responseMessage", new Schema<>().type("string").example("상품 간단 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> productWithQnAAndReviewResponseForManagerSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("name", new Schema<>().type("string").example("블루 데님 자켓"))
                        .addProperty("brand", new Schema<>().type("string").example("리바이스"))
                        .addProperty("category", new Schema<>().type("string").example("아우터/자켓"))
                        .addProperty("productNum", new Schema<>().type("string").example("PROD-2024-001"))
                        .addProperty("stock", new Schema<>().type("integer").format("int64").example(100))
                        .addProperty("soldQuantity", new Schema<>().type("integer").format("int64").example(50))
                        .addProperty("price", new Schema<>().type("integer").format("int64").example(89000))
                        .addProperty("discountRate", new Schema<>().type("integer").example(20))
                        .addProperty("defectiveStock", new Schema<>().type("integer").format("int64").example(2))
                        .addProperty("description", new Schema<>().type("string"))
                        .addProperty("createProductDate", new Schema<>().type("string").format("date-time").example("2024-01-01T10:00:00Z"))
                        .addProperty("imageUrl", new Schema<>().type("string").example("https://example.com/image.jpg"))
                        .addProperty("size", new Schema<>().type("string").example("M"))
                        .addProperty("color", new Schema<>().type("string").example("블루"))
                        .addProperty("qnADetailResponses", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/QnADetailResponse")))
                        .addProperty("reviewDetailResponses", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/ReviewDetailResponse")))
                        .addProperty("productCouponResponse", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/ProductCouponResponse"))))
                .addProperty("responseMessage", new Schema<>().type("string").example("관리자용 상품 QnA 및 리뷰 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> qnADetailResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("qnAType", new Schema<>().type("string").example("PRODUCT"))
                        .addProperty("subject", new Schema<>().type("string").example("상품 배송 문의"))
                        .addProperty("productNum", new Schema<>().type("string").example("PROD-2024-001"))
                        .addProperty("orderNum", new Schema<>().type("string").example("ORD-2024-0001"))
                        .addProperty("description", new Schema<>().type("string"))
                        .addProperty("memberEmail", new Schema<>().type("string").example("user@example.com"))
                        .addProperty("createAt", new Schema<>().type("string").format("date-time").example("2024-01-01T12:00:00Z"))
                        .addProperty("answer", new Schema<>().type("string"))
                        .addProperty("answerDate", new Schema<>().type("string").format("date-time").example("2024-01-02T15:00:00Z"))
                        .addProperty("answererEmail", new Schema<>().type("string").example("admin@example.com"))
                        .addProperty("answerStatus", new Schema<>().type("string").example("ANSWERED")))
                .addProperty("responseMessage", new Schema<>().type("string").example("QnA 세부 정보 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> qnAResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("qnAId", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("qnAType", new Schema<>().type("string").example("PRODUCT"))
                        .addProperty("subject", new Schema<>().type("string").example("상품 배송 문의"))
                        .addProperty("memberEmail", new Schema<>().type("string").example("user@example.com"))
                        .addProperty("createAt", new Schema<>().type("string").format("date-time").example("2024-01-01T12:00:00Z"))
                        .addProperty("answerStatus", new Schema<>().type("string").example("ANSWERED")))
                .addProperty("responseMessage", new Schema<>().type("string").example("QnA 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> reviewDetailResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("reviewId", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("memberEmail", new Schema<>().type("string").example("user@example.com"))
                        .addProperty("productName", new Schema<>().type("string").example("블루 데님 자켓"))
                        .addProperty("createAt", new Schema<>().type("string").format("date-time").example("2024-01-01T12:00:00Z"))
                        .addProperty("ratingType", new Schema<>().type("string").example("FIVE"))
                        .addProperty("description", new Schema<>().type("string"))
                        .addProperty("imageUrl1", new Schema<>().type("string").example("https://example.com/image1.jpg"))
                        .addProperty("imageUrl2", new Schema<>().type("string").example("https://example.com/image2.jpg"))
                        .addProperty("imageUrl3", new Schema<>().type("string").example("https://example.com/image3.jpg"))
                        .addProperty("helpful", new Schema<>().type("integer").format("int64").example(10)))
                .addProperty("responseMessage", new Schema<>().type("string").example("리뷰 세부 정보 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> reviewProductResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("productId", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("productName", new Schema<>().type("string").example("블루 데님 자켓"))
                        .addProperty("brandName", new Schema<>().type("string").example("리바이스"))
                        .addProperty("orderDate", new Schema<>().type("string").format("date-time").example("2024-01-01T10:00:00Z"))
                        .addProperty("imageUrl1", new Schema<>().type("string").example("https://example.com/image1.jpg")))
                .addProperty("responseMessage", new Schema<>().type("string").example("리뷰 상품 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> reviewResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("reviewId", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("productName", new Schema<>().type("string").example("블루 데님 자켓"))
                        .addProperty("memberEmail", new Schema<>().type("string").example("user@example.com"))
                        .addProperty("createAt", new Schema<>().type("string").format("date-time").example("2024-01-01T12:00:00Z")))
                .addProperty("responseMessage", new Schema<>().type("string").example("리뷰 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> roleResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("role", new Schema<>().type("string").example("ADMIN"))
                        .addProperty("name", new Schema<>().type("string").example("홍길동")))
                .addProperty("responseMessage", new Schema<>().type("string").example("역할 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> sellerResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("name", new Schema<>().type("string").example("리바이스"))
                        .addProperty("phoneNumber", new Schema<>().type("string").example("01012345678"))
                        .addProperty("email", new Schema<>().type("string").example("seller@example.com"))
                        .addProperty("address", new Schema<>().type("string").example("서울특별시 강남구 테헤란로")))
                .addProperty("responseMessage", new Schema<>().type("string").example("판매자 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> shippingMessageResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("message", new Schema<>().type("string").example("배송이 시작되었습니다."))
                        .addProperty("createdAt", new Schema<>().type("string").format("date-time").example("2024-01-01T12:00:00Z"))
                        .addProperty("member", new Schema<>().$ref("#/components/schemas/MemberResponse")))
                .addProperty("responseMessage", new Schema<>().type("string").example("배송 메시지 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> shippingResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("deliveryNum", new Schema<>().type("string").example("DEL-2024-0001"))
                        .addProperty("orderDate", new Schema<>().type("string").format("date-time").example("2024-01-01T10:00:00Z"))
                        .addProperty("deliveryAddress", new Schema<>().type("string").example("서울특별시 강남구 테헤란로"))
                        .addProperty("totalAmount", new Schema<>().type("integer").format("int64").example(100000))
                        .addProperty("products", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/ProductDTOForOrder")))
                        .addProperty("deliveryType", new Schema<>().type("string").example("EXPRESS"))
                        .addProperty("arrivedDate", new Schema<>().type("string").example("2024-12-31"))
                        .addProperty("departureDate", new Schema<>().type("string").example("2024-12-01"))
                        .addProperty("deliveryStatusType", new Schema<>().type("string").example("IN_TRANSIT"))
                        .addProperty("deliveryCost", new Schema<>().type("integer").format("int64").example(3000))
                        .addProperty("memberEmail", new Schema<>().type("string").example("user@example.com")))
                .addProperty("responseMessage", new Schema<>().type("string").example("배송 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> tokenResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("grantType", new Schema<>().type("string").example("Bearer"))
                        .addProperty("accessToken", new Schema<>().type("string").example("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))
                        .addProperty("refreshToken", new Schema<>().type("string").example("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))
                        .addProperty("role", new Schema<>().type("string").example("USER")))
                .addProperty("responseMessage", new Schema<>().type("string").example("토큰 생성 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> wishListDetailResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("productId", new Schema<>().type("integer").format("int64").example(1001))
                        .addProperty("productName", new Schema<>().type("string").example("블루 데님 자켓"))
                        .addProperty("productImageUrl", new Schema<>().type("string").example("https://example.com/product-image.jpg"))
                        .addProperty("productPrice", new Schema<>().type("integer").format("int64").example(89000))
                        .addProperty("liked", new Schema<>().type("boolean").example(true))
                        .addProperty("createAt", new Schema<>().type("string").format("date-time").example("2024-01-01T12:00:00Z")))
                .addProperty("responseMessage", new Schema<>().type("string").example("위시리스트 상세 조회 성공"))
                .addProperty("status", new Schema<>().type("integer").example(200));

        Schema<?> wishListResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("id", new Schema<>().type("integer").format("int64").example(1))
                        .addProperty("productId", new Schema<>().type("integer").format("int64").example(1001))
                        .addProperty("liked", new Schema<>().type("boolean").example(true))
                        .addProperty("message", new Schema<>().type("string").example("위시리스트에 추가되었습니다.")))
                .addProperty("responseMessage", new Schema<>().type("string").example("위시리스트 조회 성공"))
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

        // BadRequest 응답 스키마
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

        Schema<?> barndSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("successMessage", new Schema<>().type("string")))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(500));

        Schema<?> brandSchema = new Schema<>().type("string").example("brand1");

        Schema<?> pagedBrandListResponseSchema = new ObjectSchema()
                .addProperty("result", new ObjectSchema()
                        .addProperty("totalCount", new Schema<>().type("integer").format("int64"))
                        .addProperty("page", new Schema<>().type("integer"))
                        .addProperty("content", new ArraySchema().items(brandSchema)))
                .addProperty("responseMessage", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").example(200));



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
                        .addSchemas("CartResponseSchema", cartResponseSchema)
                        .addSchemas("CategoryResponseSchema", categoryResponseSchema)
                        .addSchemas("CouponResponseSchema", couponResponseSchema)
                        .addSchemas("EventResponseSchema", eventResponseSchema)
                        .addSchemas("MemberCouponResponseSchema", memberCouponResponseSchema)
                        .addSchemas("MemberEventResponseSchema", memberEventResponseSchema)
                        .addSchemas("MemberResponseSchema", memberResponseSchema)
                        .addSchemas("MemberResponseForUserSchema", memberResponseForUserSchema)
                        .addSchemas("MyCartResponseSchema", myCartResponseSchema)
                        .addSchemas("NotificationDetailResponseSchema", notificationDetailResponseSchema)
                        .addSchemas("NotificationResponseSchema", notificationResponseSchema)
                        .addSchemas("OrderResponseSchema", orderResponseSchema)
                        .addSchemas("ProductCouponResponseSchema", productCouponResponseSchema)
                        .addSchemas("ProductEventResponseSchema", productEventResponseSchema)
                        .addSchemas("ProductResponseSchema", productResponseSchema)
                        .addSchemas("ProductResponseForManagerSchema", productResponseForManagerSchema)
                        .addSchemas("ProductSimpleResponseSchema", productSimpleResponseSchema)
                        .addSchemas("ProductSimpleResponseForManagerSchema", productSimpleResponseForManagerSchema)
                        .addSchemas("ProductWithQnAAndReviewResponseSchema", productWithQnAAndReviewResponseSchema)
                        .addSchemas("ProductWithQnAAndReviewResponseForManagerSchema", productWithQnAAndReviewResponseForManagerSchema)
                        .addSchemas("QnADetailResponseSchema", qnADetailResponseSchema)
                        .addSchemas("QnAResponseSchema", qnAResponseSchema)
                        .addSchemas("ReviewDetailResponseSchema", reviewDetailResponseSchema)
                        .addSchemas("ReviewProductResponseSchema", reviewProductResponseSchema)
                        .addSchemas("ReviewResponseSchema", reviewResponseSchema)
                        .addSchemas("RoleResponseSchema", roleResponseSchema)
                        .addSchemas("SellerResponseSchema", sellerResponseSchema)
                        .addSchemas("ShippingMessageResponseSchema", shippingMessageResponseSchema)
                        .addSchemas("ShippingResponseSchema", shippingResponseSchema)
                        .addSchemas("TokenResponseSchema", tokenResponseSchema)
                        .addSchemas("WishListDetailResponseSchema", wishListDetailResponseSchema)
                        .addSchemas("WishListResponseSchema", wishListResponseSchema)
                        .addSchemas("MemberValidationFailedResponseSchema", memberValidationFailedResponseSchema)
                        .addSchemas("BadRequestResponseSchema", badRequestResponseSchema)
                        .addSchemas("UnauthorizedResponseSchema", unauthorizedResponseSchema)
                        .addSchemas("ForbiddenResponseSchema", forbiddenResponseSchema)
                        .addSchemas("NotFoundResponseSchema", notFoundResponseSchema)
                        .addSchemas("ConflictResponseSchema", conflictResponseSchema)
                        .addSchemas("InternalServerErrorResponseSchema", internalServerErrorResponseSchema)
                        .addSchemas("PagedBrandListResponseSchema", pagedBrandListResponseSchema)
                        .addSecuritySchemes("bearerAuth", apiKey))
                .addSecurityItem(securityRequirement);
    }
}