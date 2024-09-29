package home.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleType role = RoleType.user;
}