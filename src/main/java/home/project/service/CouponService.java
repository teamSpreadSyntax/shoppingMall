package home.project.service;

import home.project.domain.MemberCoupon;
import home.project.domain.ProductCoupon;
import home.project.dto.requestDTO.AssignCouponToMemberRequestDTO;
import home.project.dto.requestDTO.AssignCouponToProductRequestDTO;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.responseDTO.CouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {
    CouponResponse join(CreateCouponRequestDTO createCouponRequestDTO);

    Page<MemberCoupon> assignCouponToMember(AssignCouponToMemberRequestDTO assignCouponToMemberRequestDTO, Pageable pageable);
    Page<ProductCoupon> assignCouponToProduct(AssignCouponToProductRequestDTO assignCouponToProductRequestDTO, Pageable pageable);

    void assignCouponToProduct(Long couponId, Long productId);

}
