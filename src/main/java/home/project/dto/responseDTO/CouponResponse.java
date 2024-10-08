package home.project.dto.responseDTO;

import home.project.domain.MemberCoupon;
import home.project.domain.ProductCoupon;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CouponResponse {

    private Long id;

    private String name;

    private Integer discountRate;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<ProductCoupon> productCoupons;

    private List<MemberCoupon> memberCoupons;

    public CouponResponse(Long id, String name, Integer discountRate, LocalDateTime startDate, LocalDateTime endDate, List<ProductCoupon> productCoupons, List<MemberCoupon> memberCoupons){
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.productCoupons = productCoupons;
        this.memberCoupons = memberCoupons;
    }

}
