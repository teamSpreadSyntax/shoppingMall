package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "위시리스트 응답")
public class WishListResponse {

    @Schema(description = "위시리스트 ID", example = "1")
    private Long id;

    @Schema(description = "상품 ID", example = "1001")
    private Long productId;

    @Schema(description = "좋아요 여부", example = "true")
    private boolean liked;

    @Schema(description = "메시지", example = "위시리스트에 추가되었습니다.")
    private String message;

    public WishListResponse(Long id, Long productId, boolean liked, String message) {
        this.id = id;
        this.productId = productId;
        this.liked = liked;
        this.message = message;
    }
}
