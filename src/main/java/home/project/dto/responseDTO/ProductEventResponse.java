package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "상품 이벤트 응답")
public class ProductEventResponse {

    @Schema(description = "상품 이벤트 ID", example = "1")
    private Long id;

    @Schema(description = "상품 고유 번호", example = "PROD-2024-001")
    private String productNum;

    @Schema(description = "이벤트 ID", example = "101")
    private Long eventId;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    public ProductEventResponse(Long id, String productNum, Long eventId, LocalDateTime createdAt) {
        this.id = id;
        this.productNum = productNum;
        this.eventId = eventId;
        this.createdAt = createdAt;
    }
}
