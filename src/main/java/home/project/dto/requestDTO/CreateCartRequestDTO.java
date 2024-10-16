package home.project.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CreateCartRequestDTO {

    @NotEmpty(message = "카트에 넣을 상품을 입력해주세요.")
    @Schema(description = "카트에 넣을 상품", required = true)
    private List<ProductDTOForOrder> cartProducts;

    @NotNull(message = "사용할 쿠폰을 선택해주세요.")
    @Schema(description = "쿠폰")
    private Long couponId = null;

}