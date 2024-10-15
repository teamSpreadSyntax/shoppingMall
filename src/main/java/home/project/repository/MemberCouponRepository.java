package home.project.repository;


import home.project.domain.Coupon;
import home.project.domain.Member;
import home.project.domain.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    Optional<MemberCoupon> findByMemberAndCoupon(Member member, Coupon coupon);
}
