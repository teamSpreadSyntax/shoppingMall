package home.project.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class CreateCouponRequestDTO {

    @NotBlank(message = "쿠폰 이름을 입력해주세요.")
    @Schema(description = "쿠폰 이름", required = true)
    private String name;

    @NotNull(message = "할인율을 입력해주세요.")
    @Schema(description = "할인율", required = true)
    private Integer discountRate;

    @NotNull(message = "쿠폰 사용 시작날짜를 입력해주세요.")
    @Schema(description = "쿠폰 사용 시작 날짜", required = true)
    private LocalDateTime startDate;

    @NotNull(message = "쿠폰 사용 종료날짜를 입력해주세요.")
    @Schema(description = "쿠폰 사용 종료 날짜", required = true)
    private LocalDateTime endDate;

//    @NotNull(message = "쿠폰사용이 가능한 상품(들)을 입력해주세요.")
//    @Schema(description = "쿠폰 사용 가능 상품", required = true)
//    private List<ProductCoupon> productCoupons = new ArrayList<>();
//
//    @NotNull(message = "쿠폰 사용이 가능한 회원을 입력해주세요.")
//    @Schema(description = "쿠폰 사용 가능 회원", required = true)
//    private List<MemberCoupon> memberCoupons = new ArrayList<>();

}