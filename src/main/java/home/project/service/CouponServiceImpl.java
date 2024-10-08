package home.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.*;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.responseDTO.CouponResponse;
import home.project.dto.responseDTO.MemberResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService{
    private final CouponRepository couponRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;


    @Override
    @Transactional
    public CouponResponse join(CreateCouponRequestDTO createCouponRequestDTO){
        Coupon coupon = new Coupon();
        coupon.setName(createCouponRequestDTO.getName());
        coupon.setDiscountRate(createCouponRequestDTO.getDiscountRate());
        coupon.setStartDate(createCouponRequestDTO.getStartDate());
        coupon.setEndDate(createCouponRequestDTO.getEndDate());
        couponRepository.save(coupon);

        sendCouponEvent(new CouponEvent("coupon_created", coupon));

        return convertFromCouponToCouponResponse(coupon);
    }

    private void sendCouponEvent(CouponEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("coupon-events", message);
        } catch (JsonProcessingException e) {
            // 에러 처리
            e.printStackTrace();
        }
    }

    private Coupon findById(Long couponId){
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new IdNotFoundException(couponId + "(으)로 등록된 쿠폰이 없습니다."));
    }

    @Override
    @Transactional
    public void assignCouponToMember(Long couponId, Long memberId){
        Coupon coupon = findById(couponId);
        Member member = memberService.findById(memberId);
        sendCouponEvent(new CouponEvent("coupon_assigned", coupon, member));

        MemberCoupon memberCoupon = new MemberCoupon();
        memberCoupon.setCoupon(coupon);
        memberCoupon.setMember(member);

        // WebSocket을 통한 실시간 알림
        messagingTemplate.convertAndSendToUser(
                member.getEmail(),
                "/queue/coupon-assigned",
                "New coupon assigned: " + coupon.getName()
        );
    }

    @Override
    @Transactional
    public void assignCouponToProduct(Long couponId, Long productId){
        Coupon coupon = findById(couponId);
        Product product = productService.findById(productId);
        sendCouponEvent(new CouponEvent("coupon_assigned", coupon, product));

        ProductCoupon productCoupon = new ProductCoupon();
        productCoupon.setCoupon(coupon);
        productCoupon.setProduct(product);
    }


    private Page<CouponResponse> convertFromPagedCouponToPagedCouponResponse(Page<Coupon> pagedCoupon) {
        return pagedCoupon.map(coupon -> new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountRate(),
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getProductCoupons(),
                coupon.getMemberCoupons()
        ));
    }

    private CouponResponse convertFromCouponToCouponResponse(Coupon coupon){
        return new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountRate(),
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getProductCoupons(),
                coupon.getMemberCoupons()
        );
    }
}
