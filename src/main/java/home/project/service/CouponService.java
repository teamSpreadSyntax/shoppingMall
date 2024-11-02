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
import home.project.dto.responseDTO.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface CouponService {
    CouponResponse join(CreateCouponRequestDTO createCouponRequestDTO);

    Coupon findById(Long couponId);

    @Transactional
    CouponResponse updateCoupon(Long couponId, CreateCouponRequestDTO updateCouponRequestDTO);

    Page<CouponResponse> findAll(Pageable pageable);

    Page<CouponResponse> findCoupons(String name, String startDate, String endDate, String assignBy, String content, Pageable pageable);

    CouponResponse findByIdReturnCouponResponse(Long couponId);

    CouponResponse selectBestCouponForMember(Long productId);

    Page<MemberCouponResponse> assignCouponToMember(AssignCouponToMemberRequestDTO assignCouponToMemberRequestDTO, Pageable pageable);

    Page<ProductCouponResponse> assignCouponToProduct(AssignCouponToProductRequestDTO assignCouponToProductRequestDTO, Pageable pageable);

    String deleteById(Long couponId);
}
