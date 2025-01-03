package home.project.domain.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import home.project.service.util.StringListConverter;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 상품 정보를 나타내는 엔티티 클래스입니다.
 * 이 클래스는 상품의 기본 정보, 재고, 판매량 등을 저장합니다.
 */
@Entity
@Table(name = "product", uniqueConstraints = {@UniqueConstraint(columnNames = {"product_num" })})
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "productNum", "name", "brand", "category", "stock", "soldQuantity"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;

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

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Integer discountRate = 0;

    @Column(name = "defective_stock")
    private Long defectiveStock = 0L;

    @Column(name = "description", length = 1024)
    @Convert(converter = StringListConverter.class)
    private List<String> description;

    @Column(name = "createAt_product", columnDefinition = "TIMESTAMP")
    private LocalDateTime createAt;

    @Column(columnDefinition = "TEXT")
    private String mainImageFile;

    @JsonManagedReference
    @OneToMany(mappedBy = "product")
    private List<ProductCoupon> productCoupons = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "product")
    private List<ProductOrder> productOrder = new ArrayList<>();


    @Column(name = "size")
    private String size;

    @Column(name = "color")
    private String color;
}
