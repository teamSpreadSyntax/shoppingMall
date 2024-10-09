package home.project.repository;


import home.project.domain.ProductCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCouponRepository extends JpaRepository<ProductCoupon, Long> {
}
