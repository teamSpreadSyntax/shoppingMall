package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "상품 QnA 및 리뷰 응답")
public class ProductWithQnAAndReviewResponse {

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

    @Schema(description = "가격", example = "89000")
    private Long price;

    @Schema(description = "할인율", example = "20")
    private Integer discountRate;

    @Schema(description = "상품 설명")
    private List<String> description;

    @Schema(description = "이미지 URL")
    private String mainImageFile;

    @Schema(description = "좋아요 여부", example = "true")
    private boolean isLiked;

    @Schema(description = "사이즈", example = "M")
    private String size;

    @Schema(description = "색상", example = "블루")
    private String color;

    @Schema(description = "상품 쿠폰 목록")
    private List<ProductCouponResponse> productCouponResponses;

    @Schema(description = "QnA 상세 목록")
    private List<QnADetailResponse> qnADetailResponses;

    @Schema(description = "리뷰 상세 목록")
    private List<ReviewDetailResponse> reviewDetailResponses;

    public ProductWithQnAAndReviewResponse(Long id, String name, String brand, String category, String productNum, Long price, Integer discountRate, List<String> description, String mainImageFile, boolean isLiked, String size, String color, List<ProductCouponResponse> productCouponResponses, List<QnADetailResponse> qnADetailResponses, List<ReviewDetailResponse> reviewDetailResponses) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.productNum = productNum;
        this.price = price;
        this.discountRate = discountRate;
        this.description = description;
        this.mainImageFile = mainImageFile;
        this.isLiked = isLiked;
        this.size = size;
        this.color = color;
        this.productCouponResponses = productCouponResponses;
        this.qnADetailResponses = qnADetailResponses;
        this.reviewDetailResponses = reviewDetailResponses;
    }
}
