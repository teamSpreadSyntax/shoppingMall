package home.project.repository;

import home.project.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
    Page<Orders> findOrders(String orderNum, String orderDate, String productNumber, String email, String content, Pageable pageable);

}