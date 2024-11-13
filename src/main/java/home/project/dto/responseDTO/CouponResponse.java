package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CouponResponse {

    private Long id;

    private String name;

    private Integer discountRate;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String assignBy;

    private List<ProductCouponResponse> productCouponResponse;

    private List<MemberCouponResponse> memberCouponResponse;

    public CouponResponse(Long id, String name, Integer discountRate, LocalDateTime startDate, LocalDateTime endDate, String assignBy, List<ProductCouponResponse> productCouponResponse, List<MemberCouponResponse> memberCouponResponse){
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assignBy = assignBy;
        this.productCouponResponse = productCouponResponse;
        this.memberCouponResponse = memberCouponResponse;
    }

}
