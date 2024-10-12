package home.project.repository;

import home.project.domain.Coupon;
import home.project.domain.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom  {

}
