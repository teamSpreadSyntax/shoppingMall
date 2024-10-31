package home.project.dto.responseDTO;

import home.project.domain.Member;
import home.project.domain.Product;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WishListResponse {
    private Long id;

    private Long productId;

    private String message;

    public WishListResponse(Long id, Long productId, String message) {
        this.id = id;

        this.productId = productId;

        this.message = message;
    }
}
