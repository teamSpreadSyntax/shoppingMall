package home.project.service.order;

import home.project.domain.order.Cart;
import home.project.dto.responseDTO.CartResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartService {
    CartResponse join(Long productId, Integer quantity);

    Cart findById(Long cartId);

    Page<CartResponse> findAllByMemberId(Pageable pageable);

    String deleteById(Long id);

}
