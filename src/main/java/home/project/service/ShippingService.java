package home.project.service;

import home.project.domain.DeliveryStatusType;
import home.project.domain.Orders;
import home.project.domain.Shipping;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.requestDTO.CreateShippingRequestDTO;
import home.project.dto.responseDTO.OrderResponse;
import home.project.dto.responseDTO.ShippingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShippingService {
    ShippingResponse update(Long id, DeliveryStatusType deliveryStatusType);

    Shipping findById(Long shippingId);

    ShippingResponse findByIdReturnShippingResponse(Long shippingId);

//    Page<ShippingResponse> findByMemberIdReturnShippingResponse(Long shippingId, Pageable pageable);

    Page<ShippingResponse> findAll(Pageable pageable);

    Page<ShippingResponse> findShippings(String deliveryNum, String orderDate, String orderNum, String email, String content, Pageable pageable);


//    String deleteById(Long shippingId);
}
