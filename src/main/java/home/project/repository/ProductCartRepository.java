package home.project.repository;


import home.project.domain.ProductCart;
import home.project.domain.ProductCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCartRepository extends JpaRepository<ProductCart, Long> {
}
