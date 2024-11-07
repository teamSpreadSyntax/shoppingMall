package home.project.dto.requestDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTOForOrder {

    private Long productId;

    private Long price;

    private Integer quantity;



    public ProductDTOForOrder(Long productId, Long price, Integer quantity) {
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;

    }
}
