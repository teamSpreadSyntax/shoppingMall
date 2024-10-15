package home.project.repository;

import home.project.domain.Orders;
import home.project.domain.Shipping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShippingRepositoryCustom {
    Page<Shipping> findShippings(String shippingNum, String orderDate, String productNumber, String email, String content, Pageable pageable);

}