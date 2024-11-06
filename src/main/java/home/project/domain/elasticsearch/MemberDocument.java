package home.project.domain.elasticsearch;

import home.project.domain.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
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

    @Field(type = FieldType.Text, analyzer = "nori")
    private String name;

    @Field(type = FieldType.Keyword)
    private String phone;

    @Field(type = FieldType.Keyword)
    private String defaultAddress;

    @Field(type = FieldType.Keyword)
    private String secondAddress;

    @Field(type = FieldType.Keyword)
    private String thirdAddress;

    @Field(type = FieldType.Keyword)
    private String role;

    @Field(type = FieldType.Long)
    private Long accumulatedPurchase;

    @Field(type = FieldType.Long)
    private Long point;

    @Field(type = FieldType.Keyword)
    private String grade;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime birthDate;

    @Field(type = FieldType.Nested)
    private List<MemberCoupon> memberCoupons = new ArrayList<>();

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

            @Field(type = FieldType.Text)
            private String name;

            @Field(type = FieldType.Integer)
            private Integer discountRate;

            @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
            private LocalDateTime startDate;

            @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
            private LocalDateTime endDate;

            @Field(type = FieldType.Text)
            private String assignBy;
        }
    }
}

