package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "상품 간단 응답")
public class ProductSimpleResponse {

    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품 이름", example = "블루 데님 자켓")
    private String name;

    @Schema(description = "브랜드 이름", example = "리바이스")
    private String brand;

    @Schema(description = "가격", example = "89000")
    private Long price;

    @Schema(description = "할인율", example = "20")
    private Integer discountRate;

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String mainImageUrl;

    @Schema(description = "좋아요 여부", example = "true")
    private boolean isLiked;

    @Schema(description = "색상", example = "블루")
    private String color;

    public ProductSimpleResponse(Long id, String name, String brand, Long price, Integer discountRate, String mainImageUrl, boolean isLiked, String color) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.discountRate = discountRate;
        this.mainImageUrl = mainImageUrl;
        this.isLiked = isLiked;
        this.color = color;
    }
}
