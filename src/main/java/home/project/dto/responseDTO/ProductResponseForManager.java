package home.project.dto.responseDTO;

import home.project.domain.ProductCoupon;
import home.project.domain.ProductEvent;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductResponseForManager {

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

    /**
     * 상품의 재고 수량입니다.
     * 데이터베이스 컬럼 정보:
     * - 이름: stock
     * - 타입: BIGINT
     * - 제약조건: CHECK (stock >= 0)
     */
    @Check(constraints = "stock >= 0")
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
    private Long soldQuantity;

    private Long price;

    private Integer discountRate;

    private Long defectiveStock;

    private String description;

    private List<ProductCoupon> productCoupons;

    private List<ProductEvent> productEvents;

    private String imageUrl;

    public ProductResponseForManager(Long id, String name, String brand, String category, String productNum, Long stock,
                                     Long soldQuantity, Long price, Integer discountRate, Long defectiveStock, String description,
                                     List<ProductCoupon> productCoupons, List<ProductEvent> productEvents, String imageUrl) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.productNum = productNum;
        this.stock = stock;
        this.soldQuantity = soldQuantity;
        this.price = price;
        this.discountRate = discountRate;
        this.defectiveStock = defectiveStock;
        this.description = description;
        this.productCoupons = productCoupons;
        this.productEvents = productEvents;
        this.imageUrl = imageUrl;
    }
}
