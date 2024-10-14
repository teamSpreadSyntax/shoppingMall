package home.project.repository;

import home.project.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long>, OrderRepositoryCustom  {

}
