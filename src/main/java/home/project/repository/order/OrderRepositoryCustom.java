package home.project.repository.order;

import home.project.domain.order.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
    Page<Orders> findOrders(String orderNum, String orderDate, String productNumber, String email, String content, Pageable pageable);

}