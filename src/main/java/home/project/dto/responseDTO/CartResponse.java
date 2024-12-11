package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import home.project.dto.requestDTO.ProductDTOForOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "장바구니 응답")
public class CartResponse {

    @Schema(description = "사용자 이메일", example = "example@example.com")
    private String email;

    @Schema(description = "주문용 상품 목록")
    private List<ProductDTOForOrder> products;

    public CartResponse(String email, List<ProductDTOForOrder> products) {
        this.email = email;
        this.products = products;
    }
}
