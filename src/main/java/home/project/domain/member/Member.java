package home.project.domain.member;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import home.project.domain.common.WishList;
import home.project.domain.notification.Notification;
import home.project.domain.order.Orders;
import home.project.domain.product.MemberCoupon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 회원 정보를 나타내는 엔티티 클래스입니다.
 * 이 클래스는 회원의 기본 정보와 역할을 저장합니다.
 */
@Entity
@Table(name = "member", uniqueConstraints = {@UniqueConstraint(columnNames = {"phone", "email" })})
@Getter
@Setter

public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;


    @Column(name = "member_name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private MemberGenderType gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "default_address")
    private String defaultAddress;

    @Column(name = "second_address")
    private String secondAddress = null;

    @Column(name = "third_address")
    private String thirdAddress = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleType role = RoleType.user;

    @Column(name = "accumulated_purchase")
    private Long accumulatedPurchase = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_grade")
    private MemberGradeType grade = MemberGradeType.BRONZE;

    @Column(nullable = false)
    private Long point = 0L;

    @JsonManagedReference
    @OneToMany(mappedBy = "member")
    private List<MemberCoupon> memberCoupons = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProducts = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member")
    private List<Orders> orders = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishList> wishLists = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();
}