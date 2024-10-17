package home.project.service;

import home.project.domain.Cart;
import home.project.domain.Orders;
import home.project.dto.requestDTO.CreateCartRequestDTO;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.responseDTO.CartResponse;
import home.project.dto.responseDTO.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartService {
    CartResponse join(Long productId, Integer quantity);

    Cart findById(Long cartId);

    Page<CartResponse> findAll(Pageable pageable);

    String deleteById(Long id);

}
