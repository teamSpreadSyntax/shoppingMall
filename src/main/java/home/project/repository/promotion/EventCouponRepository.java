package home.project.repository.promotion;


import home.project.domain.promotion.EventCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCouponRepository extends JpaRepository<EventCoupon, Long> {
}
