package home.project.service.promotion;

import home.project.domain.product.Coupon;
import home.project.dto.requestDTO.AssignCouponToMemberRequestDTO;
import home.project.dto.requestDTO.AssignCouponToProductRequestDTO;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.responseDTO.CouponResponse;
import home.project.dto.responseDTO.MemberCouponResponse;
import home.project.dto.responseDTO.ProductCouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponService {
    CouponResponse join(CreateCouponRequestDTO createCouponRequestDTO);

    Coupon findById(Long couponId);

    CouponResponse updateCoupon(Long couponId, CreateCouponRequestDTO updateCouponRequestDTO);

    Page<CouponResponse> findAll(Pageable pageable);

    Page<CouponResponse> findCoupons(String name, String startDate, String endDate, String assignBy, String content, Pageable pageable);

    CouponResponse findByIdReturnCouponResponse(Long couponId);

    Page<CouponResponse> findAllByMemberIdReturnCouponResponse(Pageable pageable);

    CouponResponse selectBestCouponForMember(Long productId);

    Page<MemberCouponResponse> assignCouponToMember(AssignCouponToMemberRequestDTO assignCouponToMemberRequestDTO, Pageable pageable);

    Page<ProductCouponResponse> assignCouponToProduct(AssignCouponToProductRequestDTO assignCouponToProductRequestDTO, Pageable pageable);

    String deleteById(Long couponId);
}
