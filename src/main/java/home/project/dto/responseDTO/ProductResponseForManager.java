package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "관리자용 상품 상세 응답")
public class ProductResponseForManager {

    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품명", example = "블루 데님 자켓")
    private String name;

    @Schema(description = "브랜드명", example = "리바이스")
    private String brand;

    @Schema(description = "카테고리", example = "아우터/자켓")
    private String category;

    @Schema(description = "상품 고유번호", example = "PROD-2024-001")
    private String productNum;

    @Schema(description = "재고 수량", example = "100", minimum = "0")
    @Check(constraints = "stock >= 0")
    private Long stock;

    @Schema(description = "판매 수량", example = "50", minimum = "0")
    @Check(constraints = "sold_Quantity >= 0")
    private Long soldQuantity;

    @Schema(description = "가격", example = "89000")
    private Long price;

    @Schema(description = "할인율", example = "20")
    private Integer discountRate;

    @Schema(description = "불량 재고 수량", example = "2")
    private Long defectiveStock;

    @Schema(description = "상품 설명")
    private List<String> description;

    @Schema(description = "상품 등록일시")
    private LocalDateTime createProductDate;

    @Schema(description = "상품 이미지 URL")
    private String mainImageFile;

    @Schema(description = "사이즈", example = "M")
    private String size;

    @Schema(description = "색상", example = "블루")
    private String color;

    @Schema(description = "상품 쿠폰 정보 목록")
    private List<ProductCouponResponse> productCouponResponse;



    public ProductResponseForManager(Long id, String name, String brand, String category, String productNum, Long stock, Long soldQuantity, Long price, Integer discountRate, Long defectiveStock, List<String> description, LocalDateTime createProductDate, String mainImageFile, String size, String color, List<ProductCouponResponse> productCouponResponse) {
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
        this.createProductDate = createProductDate;
        this.mainImageFile = mainImageFile;
        this.size = size;
        this.color = color;
        this.productCouponResponse = productCouponResponse;

    }
}
