package home.project.repository.shipping;

import home.project.domain.delivery.Shipping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShippingRepositoryCustom {
    Page<Shipping> findShippings(String shippingNum, String orderDate, String productNumber, String email, String content, Pageable pageable);

}