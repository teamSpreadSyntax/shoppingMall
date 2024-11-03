package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishListResponse {
    private Long id;
    private Long productId;
    private boolean liked;
    private String message;

    public WishListResponse(Long id, Long productId, boolean liked, String message) {
        this.id = id;
        this.productId = productId;
        this.liked = liked;
        this.message = message;
    }
}
