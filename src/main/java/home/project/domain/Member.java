package home.project.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    /**
     * 회원의 고유 식별자입니다.
     * 데이터베이스 컬럼 정보:
     * - 타입: BIGINT
     * - 제약조건: AUTO_INCREMENT, PRIMARY KEY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회원의 이메일 주소입니다.
     * 데이터베이스 컬럼 정보:
     * - 타입: VARCHAR(255)
     * - 제약조건: UNIQUE
     * - NULL 허용
     */
    @Column(name = "email")
    private String email;

    /**
     * 회원의 비밀번호입니다.
     * 데이터베이스 컬럼 정보:
     * - 타입: VARCHAR(255) (기본값)
     */
    @Column(name = "password")
    private String password;

    /**
     * 회원의 이름입니다.
     * 데이터베이스 컬럼 정보:
     * - 타입: VARCHAR(255) (기본값)
     */
    @Column(name = "member_name")
    private String name;

    /**
     * 회원의 전화번호입니다.
     * 데이터베이스 컬럼 정보:
     * - 타입: VARCHAR(255)
     * - 제약조건: UNIQUE
     * - NULL 허용
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 회원의 권한 정보입니다.
     * Role enum을 사용하여 권한을 관리합니다.
     * 데이터베이스 컬럼 정보:
     * - 타입: VARCHAR(255) (EnumType.STRING 사용)
     */

    @Column
    private MemberGender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "default_address")
    private String defaultAddress;

    @Column(name = "secondAddress")
    private String secondAddress = null;

    @Column(name = "thirdAddress")
    private String thirdAddress = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleType role = RoleType.user;

    @Column(name = "accumulated_purchase")
    private Long accumulatedPurchase = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_grade")
    private MemberGrade grade = MemberGrade.BRONZE;

    @Column(nullable = false)
    private Long point = 0L;

    @JsonManagedReference
    @OneToMany(mappedBy = "member")
    private List<MemberCoupon> memberCoupons = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member")
    private List<MemberEvent> memberEvents = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Order> order = new ArrayList<>();

}