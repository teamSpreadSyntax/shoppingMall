package home.project.service;

import home.project.domain.Coupon;
import home.project.domain.Order;
import home.project.dto.requestDTO.AssignCouponToMemberRequestDTO;
import home.project.dto.requestDTO.AssignCouponToProductRequestDTO;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.responseDTO.CouponResponse;
import home.project.dto.responseDTO.MemberCouponResponse;
import home.project.dto.responseDTO.OrderResponse;
import home.project.dto.responseDTO.ProductCouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse join(CreateOrderRequestDTO createOrderRequestDTO);

    Order findById(Long orderId);

    Page<OrderResponse> findAll(Pageable pageable);

    Page<OrderResponse> findOrders(String name, String startDate, String endDate, String assignBy, String content, Pageable pageable);

    OrderResponse findByIdReturnOrderResponse(Long orderId);

    String deleteById(Long orderId);
}
