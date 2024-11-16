package home.project.domain.elasticsearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(indexName = "orders")
public class OrdersDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String orderNum;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime orderDate;

    @Field(type = FieldType.Long)
    private Long amount;

    @Field(type = FieldType.Long)
    private Long pointsUsed;

    @Field(type = FieldType.Long)
    private Long pointsEarned;

    @Field(type = FieldType.Nested)
    private MemberInfo member;

    @Field(type = FieldType.Nested)
    private ShippingInfo shipping;

    @Field(type = FieldType.Nested)
    private List<ProductOrderInfo> productOrders = new ArrayList<>();

    // 멤버 정보를 담는 중첩 클래스
    @Getter
    @Setter
    public static class MemberInfo {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Keyword)
        private String email;

        @MultiField(
                mainField = @Field(type = FieldType.Text, analyzer = "nori"),
                otherFields = {
                        @InnerField(suffix = "keyword", type = FieldType.Keyword)
                }
        )
        private String name;

        @Field(type = FieldType.Keyword)
        private String phone;

        @Field(type = FieldType.Keyword)
        private String defaultAddress;

        @Field(type = FieldType.Keyword)
        private String role;  // RoleType enum: user, admin, center

        @Field(type = FieldType.Keyword)
        private String grade;  // MemberGradeType enum: BRONZE, SILVER, GOLD, PLATINUM
    }

    // 배송 정보를 담는 중첩 클래스
    @Getter
    @Setter
    public static class ShippingInfo {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Keyword)
        private String deliveryType;  // DeliveryType enum: STRAIGHT_DELIVERY, ORDINARY_DELIVERY, REMOTE_DELIVERY

        @Field(type = FieldType.Keyword)
        private String deliveryNum;

        @MultiField(
                mainField = @Field(type = FieldType.Text, analyzer = "nori"),
                otherFields = {
                        @InnerField(suffix = "keyword", type = FieldType.Keyword)
                }
        )
        private String deliveryAddress;

        @Field(type = FieldType.Keyword)
        private String arrivingDate;

        @Field(type = FieldType.Keyword)
        private String arrivedDate;

        @Field(type = FieldType.Keyword)
        private String departureDate;

        @Field(type = FieldType.Long)
        private Long deliveryCost;

        @Field(type = FieldType.Keyword)
        private String deliveryStatus;  // DeliveryStatusType enum의 description

        @MultiField(
                mainField = @Field(type = FieldType.Text, analyzer = "nori"),
                otherFields = {
                        @InnerField(suffix = "keyword", type = FieldType.Keyword)
                }
        )
        private String shippingMessage;
    }

    // 주문 상품 정보를 담는 중첩 클래스
    @Getter
    @Setter
    public static class ProductOrderInfo {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Integer)
        private Integer quantity;

        @Field(type = FieldType.Long)
        private Long price;

        @Field(type = FieldType.Keyword)
        private String deliveryStatus;  // DeliveryStatusType enum의 description

        @Field(type = FieldType.Long)
        private Long productId;

        @MultiField(
                mainField = @Field(type = FieldType.Text, analyzer = "nori"),
                otherFields = {
                        @InnerField(suffix = "keyword", type = FieldType.Keyword)
                }
        )
        private String productName;

        @Field(type = FieldType.Keyword)
        private String productNum;

        @MultiField(
                mainField = @Field(type = FieldType.Text, analyzer = "nori"),
                otherFields = {
                        @InnerField(suffix = "keyword", type = FieldType.Keyword)
                }
        )
        private String brand;

        @Field(type = FieldType.Nested)
        private CategoryInfo category;
    }

    // 카테고리 정보를 담는 중첩 클래스
    @Getter
    @Setter
    public static class CategoryInfo {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Keyword)
        private String code;

        @MultiField(
                mainField = @Field(type = FieldType.Text, analyzer = "nori"),
                otherFields = {
                        @InnerField(suffix = "keyword", type = FieldType.Keyword)
                }
        )
        private String name;

        @Field(type = FieldType.Integer)
        private Integer level;

        @Field(type = FieldType.Long)
        private Long parentId;

        @MultiField(
                mainField = @Field(type = FieldType.Text, analyzer = "nori"),
                otherFields = {
                        @InnerField(suffix = "keyword", type = FieldType.Keyword)
                }
        )
        private String parentName;

        @Field(type = FieldType.Keyword)
        private String parentCode;
    }
}