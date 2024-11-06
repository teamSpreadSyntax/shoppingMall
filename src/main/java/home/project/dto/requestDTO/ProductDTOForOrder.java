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

    private String size;

    private String color;


    public ProductDTOForOrder(Long productId, Long price, Integer quantity, String size, String color) {
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
    }
}
