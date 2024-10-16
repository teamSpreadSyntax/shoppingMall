package home.project.dto.responseDTO;

import home.project.dto.requestDTO.ProductDTOForOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CartResponse {

    private Long id;

    private Long totalAmount;

    private List<ProductDTOForOrder> products;


    public CartResponse(Long id, Long totalAmount, List<ProductDTOForOrder> products) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.products = products;
    }
}
