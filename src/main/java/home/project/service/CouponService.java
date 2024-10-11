package home.project.service;

import home.project.domain.Coupon;
import home.project.domain.MemberCoupon;
import home.project.domain.ProductCoupon;
import home.project.dto.requestDTO.AssignCouponToMemberRequestDTO;
import home.project.dto.requestDTO.AssignCouponToProductRequestDTO;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.responseDTO.CouponResponse;
import home.project.dto.responseDTO.MemberCouponResponse;
import home.project.dto.responseDTO.ProductCouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {
    CouponResponse join(CreateCouponRequestDTO createCouponRequestDTO);

    Page<CouponResponse> findAll(Pageable pageable);

    CouponResponse findByIdReturnCouponResponse(Long couponId);

    Page<MemberCouponResponse> assignCouponToMember(AssignCouponToMemberRequestDTO assignCouponToMemberRequestDTO, Pageable pageable);

    Page<ProductCouponResponse> assignCouponToProduct(AssignCouponToProductRequestDTO assignCouponToProductRequestDTO, Pageable pageable);

    String deleteById(Long couponId);
}
