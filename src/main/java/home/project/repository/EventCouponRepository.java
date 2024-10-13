package home.project.repository;


import home.project.domain.EventCoupon;
import home.project.domain.ProductCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCouponRepository extends JpaRepository<EventCoupon, Long> {
}
