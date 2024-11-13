package home.project.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CreateOrderRequestDTO {

    @NotEmpty(message = "주문 항목을 입력해주세요.")
    @Schema(description = "주문 항목 리스트", required = true)
    private List<ProductDTOForOrder> productOrders;

    @NotNull(message = "배송정보를 입력해주세요.")
    @Schema(description = "배송정보", required = true)
    private CreateShippingRequestDTO shippingInfo;

    @NotNull(message = "사용할 포인트를 입력해주세요.")
    @Schema(description = "사용할 포인트")
    private Long pointsUsed = 0L;

    @NotNull(message = "사용할 쿠폰을 선택해주세요.")
    @Schema(description = "쿠폰")
    private Long couponId = null;

}