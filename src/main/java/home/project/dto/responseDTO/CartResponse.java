package home.project.dto.responseDTO;

import home.project.dto.requestDTO.ProductDTOForOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartResponse {

    private String email;

    private List<ProductDTOForOrder> products;

    public CartResponse(String email, List<ProductDTOForOrder> products) {
        this.email = email;
        this.products = products;
    }
}
