package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "관리자용 상품 QnA 및 리뷰 응답")
public class ProductWithQnAAndReviewResponseForManager {

    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품 이름", example = "블루 데님 자켓")
    private String name;

    @Schema(description = "브랜드 이름", example = "리바이스")
    private String brand;

    @Schema(description = "카테고리", example = "아우터/자켓")
    private String category;

    @Schema(description = "상품 고유 번호", example = "PROD-2024-001")
    private String productNum;

    @Schema(description = "재고 수량", example = "100")
    private Long stock;

    @Schema(description = "판매 수량", example = "50")
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
    private String mainImageUrl;

    @Schema(description = "사이즈", example = "M")
    private String size;

    @Schema(description = "색상", example = "블루")
    private String color;

    @Schema(description = "QnA 상세 목록")
    private List<QnADetailResponse> qnADetailResponses;

    @Schema(description = "리뷰 상세 목록")
    private List<ReviewDetailResponse> reviewDetailResponses;

    @Schema(description = "상품 쿠폰 목록")
    private List<ProductCouponResponse> productCouponResponse;

    public ProductWithQnAAndReviewResponseForManager(Long id, String name, String brand, String category, String productNum, Long stock, Long soldQuantity, Long price, Integer discountRate, Long defectiveStock, List<String> description, LocalDateTime createProductDate, String mainImageUrl, String size, String color, List<QnADetailResponse> qnADetailResponses, List<ReviewDetailResponse> reviewDetailResponses, List<ProductCouponResponse> productCouponResponse) {
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
        this.mainImageUrl = mainImageUrl;
        this.size = size;
        this.color = color;
        this.qnADetailResponses = qnADetailResponses;
        this.reviewDetailResponses = reviewDetailResponses;
        this.productCouponResponse = productCouponResponse;
    }
}
