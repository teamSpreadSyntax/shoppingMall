package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class ProductSimpleResponsesForCart {

    private Page<ProductSimpleResponseForCart> ProductSimpleResponsesForCart;


    public ProductSimpleResponsesForCart(Page<ProductSimpleResponseForCart> productSimpleResponsesForCart) {
        ProductSimpleResponsesForCart = productSimpleResponsesForCart;
    }
}
