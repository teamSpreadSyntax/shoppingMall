package home.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.*;
import home.project.dto.CouponEventDTO;
import home.project.dto.requestDTO.AssignCouponToMemberRequestDTO;
import home.project.dto.requestDTO.AssignCouponToProductRequestDTO;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.responseDTO.*;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.*;
import home.project.util.StringBuilderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService{
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final ProductCouponRepository productCouponRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Converter converter;
    private final MemberService memberService;


    @Override
    @Transactional
    public CouponResponse join(CreateCouponRequestDTO createCouponRequestDTO){
        Coupon coupon = new Coupon();
        coupon.setName(createCouponRequestDTO.getName());
        coupon.setDiscountRate(createCouponRequestDTO.getDiscountRate());
        coupon.setStartDate(createCouponRequestDTO.getStartDate());
        coupon.setEndDate(createCouponRequestDTO.getEndDate());
        couponRepository.save(coupon);

        sendCouponEvent(new CouponEventDTO("coupon_created", coupon.getId()));

        return converter.convertFromCouponToCouponResponse(coupon);
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(Long couponId, CreateCouponRequestDTO updateCouponRequestDTO) {

        Coupon coupon = findById(couponId);

        coupon.setName(updateCouponRequestDTO.getName());
        coupon.setDiscountRate(updateCouponRequestDTO.getDiscountRate());
        coupon.setStartDate(updateCouponRequestDTO.getStartDate());
        coupon.setEndDate(updateCouponRequestDTO.getEndDate());

        couponRepository.save(coupon);
        return converter.convertFromCouponToCouponResponse(coupon);

    }

    @Override
    public Page<CouponResponse> findAll(Pageable pageable) {
        Page<Coupon> pagedCoupon= couponRepository.findAll(pageable);
        return converter.convertFromPagedCouponToPagedCouponResponse(pagedCoupon);
    }

    @Override
    public CouponResponse findByIdReturnCouponResponse(Long couponId) {
        return converter.convertFromCouponToCouponResponse(findById(couponId));
    }


    private void sendCouponEvent(CouponEventDTO event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("coupon-events", message);
        } catch (JsonProcessingException e) {
            // 에러 처리
            e.printStackTrace();
        }
    }

    @Override
    public Coupon findById(Long couponId){
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new IdNotFoundException(couponId + "(으)로 등록된 쿠폰이 없습니다."));
    }

    @Override
    public Page<CouponResponse> findCoupons(String name, String startDate, String endDate, String assignBy, String content, Pageable pageable) {

        Page<Coupon> pagedCoupon = couponRepository.findCoupons(name, startDate, endDate, assignBy, content, pageable);

        return converter.convertFromPagedCouponToPagedCouponResponse(pagedCoupon);
    }

    @Override
    public CouponResponse selectBestCouponForMember(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        List<Coupon> availableCoupons = couponRepository.findAvailableCoupons(member.getId(), productId);
        Coupon bestCoupon = selectBestCoupon(availableCoupons);

        return bestCoupon != null ? converter.convertFromCouponToCouponResponse(bestCoupon) : null;
    }

    public Coupon selectBestCoupon(List<Coupon> coupons) {
        return coupons.stream()
                .filter(coupon -> isValidCoupon(coupon))
                .max(Comparator.comparingInt(Coupon::getDiscountRate)
                        .thenComparing(Coupon::getStartDate))
                .orElse(null);
    }

    private boolean isValidCoupon(Coupon coupon) {
        LocalDateTime now = LocalDateTime.now();
        return (coupon.getStartDate().isBefore(now) || coupon.getStartDate().isEqual(now)) &&
                coupon.getEndDate().isAfter(now);
    }


    @Override
    @Transactional
    public Page<MemberCouponResponse> assignCouponToMember(AssignCouponToMemberRequestDTO assignCouponToMemberRequestDTO, Pageable pageable){
        Coupon coupon = findById(assignCouponToMemberRequestDTO.getCouponId());

        String assignCondition = StringBuilderUtil.buildAssignCondition(assignCouponToMemberRequestDTO);
        coupon.setAssignBy(assignCondition);
        couponRepository.save(coupon);

        Page<Member> targetMembers = getTargetMembers(assignCouponToMemberRequestDTO, pageable);

        return targetMembers.map(member -> {
            MemberCoupon memberCoupon = new MemberCoupon();
            memberCoupon.setMember(member);
            memberCoupon.setCoupon(coupon);
            memberCoupon.setIssuedAt(LocalDateTime.now());

            MemberCoupon savedMemberCoupon = memberCouponRepository.save(memberCoupon);

            sendCouponEvent(new CouponEventDTO("coupon_assigned_to_member", coupon.getId(), member.getId()));

            return new MemberCouponResponse(
                    savedMemberCoupon.getId(),
                    member.getEmail(),
                    coupon.getId(),
                    savedMemberCoupon.getIssuedAt(),
                    null,
                    false
            );
        });
    }

    @Override
    @Transactional
    public Page<ProductCouponResponse> assignCouponToProduct(AssignCouponToProductRequestDTO assignCouponToProductRequestDTO, Pageable pageable){
        Coupon coupon = findById(assignCouponToProductRequestDTO.getCouponId());

        String assignCondition = StringBuilderUtil.buildAssignCondition(assignCouponToProductRequestDTO);
        coupon.setAssignBy(assignCondition);
        couponRepository.save(coupon);

        Page<Product> targetProducts = getTargetProducts(assignCouponToProductRequestDTO, pageable);

        return targetProducts.map(product -> {
            ProductCoupon productCoupon = new ProductCoupon();
            productCoupon.setProduct(product);
            productCoupon.setCoupon(coupon);
            productCoupon.setIssuedAt(LocalDateTime.now());

            ProductCoupon savedProductCoupon = productCouponRepository.save(productCoupon);

            sendCouponEvent(new CouponEventDTO("coupon_assigned_to_product", coupon.getId(), null, product.getId()));

            return new ProductCouponResponse(
                    savedProductCoupon.getId(),
                    product.getProductNum(),
                    coupon.getId(),
                    savedProductCoupon.getIssuedAt(),
                    null,
                    false
            );
        });
    }

    private Page<Member> getTargetMembers(AssignCouponToMemberRequestDTO assignCouponToMemberRequestDTO, Pageable pageable) {
        if (assignCouponToMemberRequestDTO.getAssignType() == AssignType.SPECIFIC_MEMBERS) {
            return memberRepository.findMembers(
                    assignCouponToMemberRequestDTO.getName(),
                    assignCouponToMemberRequestDTO.getEmail(),
                    assignCouponToMemberRequestDTO.getPhone(),
                    assignCouponToMemberRequestDTO.getRole(),
                    assignCouponToMemberRequestDTO.getContent(),
                    pageable
                    );
        } else if (assignCouponToMemberRequestDTO.getAssignType() == AssignType.ALL) {
            return memberRepository.findAll(pageable);
        } else {
            throw new IllegalArgumentException("assign type을 확인해주세요. (SPECIFIC_MEMBERS : 특정 회원(들)에게 쿠폰 부여, ALL : 모든 회원에게 쿠폰 부여.)");
        }
    }
    private Page<Product> getTargetProducts(AssignCouponToProductRequestDTO assignCouponToProductRequestDTO, Pageable pageable) {
        if (assignCouponToProductRequestDTO.getAssignType() == AssignType.SPECIFIC_PRODUCTS) {
            return productRepository.findProducts(
                    assignCouponToProductRequestDTO.getBrand(),
                    assignCouponToProductRequestDTO.getCategory(),
                    assignCouponToProductRequestDTO.getProductName(),
                    assignCouponToProductRequestDTO.getContent(),
                    pageable
            );
        } else if (assignCouponToProductRequestDTO.getAssignType() == AssignType.ALL) {
            return productRepository.findAll(pageable);
        } else {
            throw new IllegalArgumentException("assign type을 확인해주세요. (SPECIFIC_PRODUCTS : 특정 상품(들)에 쿠폰 부여, ALL : 모든 상품에 쿠폰 부여.)");
        }


    }

    @Override
    @Transactional
    public String deleteById(Long couponId) {
        String name = findById(couponId).getName();
        couponRepository.deleteById(couponId);
        return name;
    }



}
