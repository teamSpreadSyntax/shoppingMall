package home.project.repository.promotion;

import home.project.domain.order.Cart;
import home.project.domain.product.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponRepositoryCustom {
    Page<Coupon> findCoupons(String name, String startDate, String endDate, String assignBy, String content, Pageable pageable);
    Page<Coupon> findAllByMemberId(Long memberId, Pageable pageable);
}