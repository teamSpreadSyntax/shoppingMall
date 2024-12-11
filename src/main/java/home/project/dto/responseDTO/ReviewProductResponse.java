package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "리뷰 상품 응답")
public class ReviewProductResponse {

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "상품 이름", example = "블루 데님 자켓")
    private String productName;

    @Schema(description = "브랜드 이름", example = "리바이스")
    private String brandName;

    @Schema(description = "주문 날짜")
    private LocalDateTime orderDate;

    @Schema(description = "이미지 URL 1", example = "https://example.com/image1.jpg")
    private String imageUrl1;

    public ReviewProductResponse(Long productId, String productName, String brandName, LocalDateTime orderDate, String imageUrl1) {
        this.productId = productId;
        this.productName = productName;
        this.brandName = brandName;
        this.orderDate = orderDate;
        this.imageUrl1 = imageUrl1;
    }
}
