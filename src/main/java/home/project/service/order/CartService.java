package home.project.service.order;

import home.project.domain.order.Cart;
import home.project.dto.requestDTO.ProductDTOForOrder;
import home.project.dto.responseDTO.CartResponse;
import home.project.dto.responseDTO.MyCartResponse;
import home.project.dto.responseDTO.ProductSimpleResponseForCart;
import home.project.dto.responseDTO.ProductSimpleResponsesForCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartService {
    CartResponse join(Long productId, Integer quantity);

    Cart findById(Long cartId);

    Page<ProductSimpleResponseForCart> findAllByMemberId(Pageable pageable);

    String deleteByProductId(Long productid);

}
