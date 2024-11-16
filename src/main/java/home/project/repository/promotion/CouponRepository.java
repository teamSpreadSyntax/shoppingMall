package home.project.repository.promotion;

import home.project.domain.product.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom  {
    @Query("SELECT c FROM Coupon c " +
            "LEFT JOIN c.memberCoupons mc ON mc.member.id = :memberId " +
            "LEFT JOIN c.productCoupons pc ON pc.product.id = :productId " +
            "WHERE mc.member.id = :memberId OR pc.product.id = :productId")
    List<Coupon> findAvailableCoupons(@Param("memberId") Long memberId, @Param("productId") Long productId);

    Page<Coupon> findAllByMemberId(Long memberId, Pageable pageable);
}
