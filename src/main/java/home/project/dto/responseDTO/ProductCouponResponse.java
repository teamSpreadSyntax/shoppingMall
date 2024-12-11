package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "상품 쿠폰 응답")
public class ProductCouponResponse {

    @Schema(description = "쿠폰 ID", example = "1")
    private Long id;

    @Schema(description = "상품 번호", example = "PROD-2024-001")
    private String productNum;

    @Schema(description = "쿠폰 ID", example = "101")
    private Long couponId;

    @Schema(description = "발급일")
    private LocalDateTime issuedAt;

    @Schema(description = "사용일")
    private LocalDateTime usedAt;

    @Schema(description = "할인율", example = "20")
    private Integer discountRate;

    @Schema(description = "사용 여부", example = "false")
    private boolean isUsed;

    public ProductCouponResponse(Long id, String productNum, Long couponId, LocalDateTime issuedAt, LocalDateTime usedAt, boolean isUsed) {
        this.id = id;
        this.productNum = productNum;
        this.couponId = couponId;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
        this.isUsed = isUsed;
    }
}
