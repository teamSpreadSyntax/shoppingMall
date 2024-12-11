package home.project.dto.responseDTO;

import home.project.dto.requestDTO.ProductDTOForOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "내 장바구니 응답")
public class MyCartResponse {

    @Schema(description = "상품 목록")
    private List<ProductDTOForOrder> products;

    public MyCartResponse(List<ProductDTOForOrder> products) {
        this.products = products;
    }
}
