package home.project.repository;

import home.project.domain.Cart;
import home.project.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom  {

}
