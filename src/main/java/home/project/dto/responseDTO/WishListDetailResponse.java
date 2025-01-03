package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "위시리스트 상세 응답")
public class WishListDetailResponse {

    @Schema(description = "위시리스트 ID", example = "1")
    private Long id;

    @Schema(description = "상품 ID", example = "1001")
    private Long productId;

    @Schema(description = "상품 이름", example = "블루 데님 자켓")
    private String productName;

    @Schema(description = "상품 이미지 URL", example = "https://example.com/product-image.jpg")
    private String mainImageFile;

    @Schema(description = "상품 가격", example = "89000")
    private Long productPrice;

    @Schema(description = "좋아요 여부", example = "true")
    private boolean liked;

    @Schema(description = "생성일")
    private LocalDateTime createAt;

    public WishListDetailResponse(Long id, Long productId, String productName, String mainImageFile, Long productPrice, boolean liked, LocalDateTime createAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.mainImageFile = mainImageFile;
        this.productPrice = productPrice;
        this.liked = liked;
        this.createAt = createAt;
    }
}
