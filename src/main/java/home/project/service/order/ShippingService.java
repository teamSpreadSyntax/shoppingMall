package home.project.service.order;

import home.project.domain.delivery.DeliveryStatusType;
import home.project.domain.delivery.Shipping;
import home.project.dto.responseDTO.ShippingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShippingService {
    ShippingResponse update(Long id, DeliveryStatusType deliveryStatusType);

    Shipping findById(Long shippingId);

    ShippingResponse findByIdReturnShippingResponse(Long shippingId);

//    Page<ShippingResponse> findByMemberIdReturnShippingResponse(Long shippingId, Pageable pageable);

    Page<ShippingResponse> findAll(Pageable pageable);

    Page<ShippingResponse> findShippings(String deliveryNum, String orderDate, String productNum, String email, String content, Pageable pageable);


//    String deleteById(Long shippingId);
}
