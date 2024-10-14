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

    @NotNull(message = "주문 회원 이메일을 입력해주세요.")
    @Schema(description = "이메일", required = true)
    private String email;

    @NotEmpty(message = "주문 항목을 입력해주세요.")
    @Schema(description = "주문 항목 리스트", required = true)
    private List<ProductDTOForOrder> productOrders;

    @NotNull(message = "배송정보를 입력해주세요.")
    @Schema(description = "배송정보", required = true)
    private CreateShippingRequestDTO shippingInfo;


}