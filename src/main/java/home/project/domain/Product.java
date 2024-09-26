package home.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

/**
 * 상품 정보를 나타내는 엔티티 클래스입니다.
 * 이 클래스는 상품의 기본 정보, 재고, 판매량 등을 저장합니다.
 */
@Entity
@Table(name = "product", uniqueConstraints = {@UniqueConstraint(columnNames = {"product_num" })})
@Getter
@Setter
public class Product {

    /**
     * 상품의 고유 식별자입니다.
     * 데이터베이스 컬럼 정보:
     * - 타입: BIGINT
     * - 제약조건: AUTO_INCREMENT, PRIMARY KEY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 상품의 이름입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: product_name
     * - 타입: VARCHAR(255) (기본값)
     */
    @Column(name = "product_name")
    private String name;

    /**
     * 상품의 브랜드입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: brand
     * - 타입: VARCHAR(255) (기본값)
     */
    @Column(name = "brand")
    private String brand;

    /**
     * 상품의 카테고리입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: category
     * - 타입: VARCHAR(255) (기본값)
     */
    @Column(name = "category")
    private String category;

    /**
     * 상품의 고유 번호입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: product_num
     * - 타입: VARCHAR(255) (기본값)
     * - 제약조건: UNIQUE
     */
    @Column(name = "product_num")
    private String productNum;

    /**
     * 상품의 재고 수량입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: stock
     * - 타입: BIGINT
     * - 제약조건: CHECK (stock >= 0)
     */
    @Check(constraints = "stock >= 0")
    @Column(name = "stock")
    private Long stock;

    /**
     * 상품의 판매 수량입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: sold_Quantity
     * - 타입: BIGINT
     * - 제약조건: CHECK (sold_Quantity >= 0)
     * - 기본값: 0
     */
    @Check(constraints = "sold_Quantity >= 0")
    @Column(name = "sold_Quantity")
    private Long soldQuantity = 0L;
}