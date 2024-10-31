package home.project.domain.elasticsearch;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import home.project.domain.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
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
    private MemberGenderType gender;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate birthDate;

    @Field(type = FieldType.Keyword)
    private String defaultAddress;

    @Field(type = FieldType.Keyword)
    private String secondAddress = null;

    @Field(type = FieldType.Keyword)
    private String thirdAddress = null;

    @Field(type = FieldType.Keyword)
    private RoleType role = RoleType.user;

    @Field(type = FieldType.Long)
    private Long accumulatedPurchase = 0L;

    @Field(type = FieldType.Keyword)
    private MemberGradeType grade = MemberGradeType.BRONZE;

    @Field(type = FieldType.Long)
    private Long point = 0L;

    @Field(type = FieldType.Nested)
    private List<MemberCoupon> memberCoupons = new ArrayList<>();

    @Field(type = FieldType.Nested)
    private List<MemberEvent> memberEvents = new ArrayList<>();

    @Field(type = FieldType.Nested)
    private List<Orders> orders = new ArrayList<>();

    @Field(type = FieldType.Nested)
    private List<WishList> wishLists = new ArrayList<>();

}