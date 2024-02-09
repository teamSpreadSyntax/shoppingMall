package home.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "member")
@Getter
@Setter
public class Member {
    @Id
    private Long idd;

    @Column(name = "name")
    private String name;
//
//    @Column(name = "sex")
//    private String sex;
//
//    @Column(name = "birth")
//    private Date birth;
//
//    @Column(name = "age")
//    private Integer age;
//
//    @Column(name = "address_num")
//    private String address_num;
//
//    @Column(name = "address")
//    private String address;
//
//    @Column(name = "address_detail")
//    private String address_detail;
//
//    @Column(name = "address_name")
//    private String address_name;
//
//    @Column(name = "phone")
//    private String phone;
//
//    @Column(name = "mileage")
//    private Integer mileage;
//
//    @Column(name = "coupon")
//    private String coupon;
//
//    @Column(name = "total")
//    private String total;
}
