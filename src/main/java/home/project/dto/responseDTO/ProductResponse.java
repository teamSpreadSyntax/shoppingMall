package home.project.dto.responseDTO;

import home.project.domain.Category;
import home.project.domain.ProductCoupon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.util.List;

@Getter
@Setter
public class ProductResponse {

    /**
     * 상품의 고유 식별자입니다.
     * 데이터베이스 컬럼 정보:
     * - 타입: BIGINT
     * - 제약조건: AUTO_INCREMENT, PRIMARY KEY
     */
    private Long id;

    /**
     * 상품의 이름입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: product_name
     * - 타입: VARCHAR(255) (기본값)
     */
    private String name;

    /**
     * 상품의 브랜드입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: brand
     * - 타입: VARCHAR(255) (기본값)
     */
    private String brand;

    /**
     * 상품의 카테고리입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: category
     * - 타입: VARCHAR(255) (기본값)
     */
    private String category;

    /**
     * 상품의 고유 번호입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: product_num
     * - 타입: VARCHAR(255) (기본값)
     * - 제약조건: UNIQUE
     */
    private String productNum;

    private Long price;

    private String description;

    private List<ProductCoupon> productCoupons;

    public ProductResponse(Long id, String name, String brand, String category, String productNum, Long price, List<ProductCoupon> productCoupons, String description) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.productNum = productNum;
        this.price = price;
        this.description = description;
        this.productCoupons = productCoupons;
    }
}
