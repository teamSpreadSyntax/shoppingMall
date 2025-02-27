package home.project.service.order;

import home.project.domain.order.Orders;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.responseDTO.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse join(CreateOrderRequestDTO createOrderRequestDTO);

    Orders findById(Long orderId);

    Orders findByOrderNum(String OrderNum);

    Page<OrderResponse> findByMemberId(Pageable pageable);

    Page<OrderResponse> findAll(Pageable pageable);

    Page<OrderResponse> findOrders(String orderNum, String orderDate, String productNumber, String email, String content, Pageable pageable);

    OrderResponse findByIdReturnOrderResponse(Long orderId);

    String deleteById(Long orderId);

    void confirmPurchase(Long orderId);
}
