package home.project.repository;

import home.project.domain.Coupon;
import home.project.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
    Page<Order> findOrders(String name, String startDate, String endDate, String assignBy, String content, Pageable pageable);

}