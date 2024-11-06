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
@Document(indexName = "products")
public class ProductDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String name;

    @Field(type = FieldType.Keyword)
    private String brand;

    @Field(type = FieldType.Keyword)
    private String productNum;

    @Field(type = FieldType.Long)
    private Long price;

    @Field(type = FieldType.Integer)
    private Integer discountRate;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String description;

    @Field(type = FieldType.Keyword)
    private String imageUrl;

    @Field(type = FieldType.Long)
    private Long stock;

    @Field(type = FieldType.Long)
    private Long soldQuantity;

    @Field(type = FieldType.Long)
    private Long defectiveStock;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createAt;

    @Field(type = FieldType.Keyword)
    private String size;

    @Field(type = FieldType.Keyword)
    private String color;

    @Field(type = FieldType.Nested)
    private List<ProductCoupon> productCoupons = new ArrayList<>();

    @Getter
    @Setter
    public static class ProductCoupon {
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

