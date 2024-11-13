package home.project.repository.promotion;


import home.project.domain.product.ProductCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCouponRepository extends JpaRepository<ProductCoupon, Long> {
}
