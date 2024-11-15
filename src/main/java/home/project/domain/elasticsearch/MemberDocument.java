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
@Document(indexName = "members")
public class MemberDocument {
    @Id
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
    private String gender;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String defaultAddress;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String secondAddress;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String thirdAddress;

    @Field(type = FieldType.Keyword)
    private String role;  // RoleType enum: user, admin, center

    @Field(type = FieldType.Long, docValues = true)
    private Long accumulatedPurchase = 0L;

    @Field(type = FieldType.Long, docValues = true)
    private Long point = 0L;

    @Field(type = FieldType.Keyword)
    private String grade;  // MemberGradeType enum: BRONZE, SILVER, GOLD, PLATINUM

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime birthDate;

    @Field(type = FieldType.Nested)
    private List<MemberCoupon> memberCoupons = new ArrayList<>();

    @Field(type = FieldType.Nested)
    private List<OrderInfo> orders = new ArrayList<>();

    @Getter
    @Setter
    public static class MemberCoupon {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
        private LocalDateTime issuedAt;

        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
        private LocalDateTime usedAt;

        @Field(type = FieldType.Boolean)
        private boolean isUsed;

        @Field(type = FieldType.Nested)
        private Coupon coupon;

        @Getter
        @Setter
        public static class Coupon {
            @Field(type = FieldType.Long)
            private Long id;

            @MultiField(
                    mainField = @Field(type = FieldType.Text, analyzer = "nori"),
                    otherFields = {
                            @InnerField(suffix = "keyword", type = FieldType.Keyword)
                    }
            )
            private String name;

            @Field(type = FieldType.Integer)
            private Integer discountRate;

            @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
            private LocalDateTime startDate;

            @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
            private LocalDateTime endDate;

            @MultiField(
                    mainField = @Field(type = FieldType.Text, analyzer = "nori"),
                    otherFields = {
                            @InnerField(suffix = "keyword", type = FieldType.Keyword)
                    }
            )
            private String assignBy;
        }
    }

    @Getter
    @Setter
    public static class OrderInfo {
        @Field(type = FieldType.Long)
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

        @Field(type = FieldType.Keyword)
        private String deliveryStatus; // DeliveryStatusType enum의 description
    }
}