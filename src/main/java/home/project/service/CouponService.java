package home.project.service;

import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.responseDTO.CouponResponse;

public interface CouponService {
    CouponResponse join(CreateCouponRequestDTO createCouponRequestDTO);

    void assignCouponToMember(Long couponId, Long memberId);

    void assignCouponToProduct(Long couponId, Long productId);

}
