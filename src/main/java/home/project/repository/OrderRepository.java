package home.project.repository;

import home.project.domain.Coupon;
import home.project.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom  {

}
