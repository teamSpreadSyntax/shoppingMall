package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "쿠폰 응답")
public class CouponResponse {

    @Schema(description = "쿠폰 ID", example = "1")
    private Long id;

    @Schema(description = "쿠폰 이름", example = "할인 쿠폰")
    private String name;

    @Schema(description = "할인율", example = "20")
    private Integer discountRate;

    @Schema(description = "쿠폰 시작 날짜")
    private LocalDateTime startDate;

    @Schema(description = "쿠폰 종료 날짜")
    private LocalDateTime endDate;

    @Schema(description = "발급자", example = "관리자")
    private String assignBy;

    @Schema(description = "상품 쿠폰 목록")
    private List<ProductCouponResponse> productCouponResponse;

    @Schema(description = "회원 쿠폰 목록")
    private List<MemberCouponResponse> memberCouponResponse;

    public CouponResponse(Long id, String name, Integer discountRate, LocalDateTime startDate, LocalDateTime endDate, String assignBy, List<ProductCouponResponse> productCouponResponse, List<MemberCouponResponse> memberCouponResponse) {
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assignBy = assignBy;
        this.productCouponResponse = productCouponResponse;
        this.memberCouponResponse = memberCouponResponse;
    }
}
