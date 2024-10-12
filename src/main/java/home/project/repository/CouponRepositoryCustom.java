package home.project.repository;

import home.project.domain.Coupon;
import home.project.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponRepositoryCustom {
    Page<Coupon> findCoupons(String name, String startDate, String endDate, String assignBy, String content, Pageable pageable);

}