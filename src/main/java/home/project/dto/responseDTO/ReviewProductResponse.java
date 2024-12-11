package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewProductResponse {

    private Long productId;

    private String productName;

    private String brandName;

    private LocalDateTime orderDate;

    private String imageUrl1;

    public ReviewProductResponse(Long productId, String productName, String brandName, LocalDateTime orderDate, String imageUrl1) {
        this.productId = productId;
        this.productName = productName;
        this.brandName = brandName;
        this.orderDate = orderDate;
        this.imageUrl1 = imageUrl1;
    }
}
