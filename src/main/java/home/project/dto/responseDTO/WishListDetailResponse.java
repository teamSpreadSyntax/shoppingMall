package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WishListDetailResponse {
    private Long id;

    private Long productId;

    private String productName;

    private String productImageUrl;

    private Long productPrice;

    private LocalDateTime createdAt;

    public WishListDetailResponse(Long id, Long productId, String productName, String productImageUrl, Long productPrice, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.productPrice = productPrice;
        this.createdAt = createdAt;
    }
}