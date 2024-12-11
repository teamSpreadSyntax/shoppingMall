package home.project.dto.responseDTO;

import home.project.dto.requestDTO.ProductDTOForOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyCartResponse {

    private List<ProductDTOForOrder> products;

    public MyCartResponse(List<ProductDTOForOrder> products) {
        this.products = products;
    }
}
