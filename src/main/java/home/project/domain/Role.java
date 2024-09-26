package home.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자의 역할을 나타내는 엔티티 클래스입니다.
 * 이 클래스는 사용자의 권한 수준을 정의합니다.
 */
@Entity
@Table(name = "role")
@Getter
@Setter
public class Role {

    /**
     * 역할의 고유 식별자입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: id
     * - 타입: BIGINT
     * - 제약조건: PRIMARY KEY, AUTO_INCREMENT
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자의 권한 유형입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: role
     * - 타입: VARCHAR(255)
     * - 기본값: 'user'
     * - 제약조건: NOT NULL
     * <p>
     * 이 필드는 RoleType 열거형을 사용하며, 기본값은 RoleType.user입니다.
     * 데이터베이스에는 열거형의 문자열 값으로 저장됩니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleType role = RoleType.user;

}