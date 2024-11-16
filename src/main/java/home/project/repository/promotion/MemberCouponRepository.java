package home.project.repository.promotion;


import home.project.domain.member.Member;
import home.project.domain.product.Coupon;
import home.project.domain.product.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    Optional<MemberCoupon> findByMemberAndCoupon(Member member, Coupon coupon);
}
