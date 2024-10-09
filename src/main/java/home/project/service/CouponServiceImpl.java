package home.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.*;
import home.project.dto.requestDTO.AssignCouponToMemberRequestDTO;
import home.project.dto.requestDTO.AssignCouponToProductRequestDTO;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.responseDTO.CouponResponse;
import home.project.dto.responseDTO.MemberResponse;
import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.ProductResponseForManager;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static home.project.util.CategoryMapper.getCode;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService{
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final ProductCouponRepository productCouponRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
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
    public Page<MemberCoupon> assignCouponToMember(AssignCouponToMemberRequestDTO assignCouponToMemberRequestDTO, Pageable pageable){
        Coupon coupon = findById(assignCouponToMemberRequestDTO.getCouponId());

        Page<Member> targetMembers = getTargetMembers(assignCouponToMemberRequestDTO, pageable);

        for (Member member : targetMembers) {
            MemberCoupon memberCoupon = new MemberCoupon();
            memberCoupon.setMember(member);
            memberCoupon.setCoupon(coupon);
            memberCoupon.setIssuedAt(LocalDateTime.now());

            memberCouponRepository.save(memberCoupon);

            sendCouponEvent(new CouponEvent("coupon_assigned_to_member", coupon));
            sendWebSocketNotification(member.getEmail(), "New coupon assigned: " + coupon.getName());
        }
        return convertFromPagedMemberAndCouponToPagedMemberCoupon(targetMembers, coupon);
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
            throw new IllegalArgumentException("Invalid assign type");
        }
    }

    @Override
    @Transactional
    public Page<ProductCoupon> assignCouponToProduct(AssignCouponToProductRequestDTO assignCouponToProductRequestDTO, Pageable pageable){
        Coupon coupon = findById(assignCouponToProductRequestDTO.getCouponId());

        Page<Product> targetProducts = getTargetProducts(assignCouponToProductRequestDTO, pageable);
        for (Product product : targetProducts) {
            ProductCoupon productCoupon = new ProductCoupon();
            productCoupon.setProduct(product);
            productCoupon.setCoupon(coupon);
            productCoupon.setIssuedAt(LocalDateTime.now());

            productCouponRepository.save(productCoupon);

            sendCouponEvent(new CouponEvent("coupon_assigned_to_member", coupon));
            sendWebSocketNotification(product.getProductNum(), "New coupon assigned: " + coupon.getName());
        }
        return convertFromPagedProductAndCouponToPagedProductCoupon(targetProducts, coupon);
    }

    private Page<Product> getTargetProducts(AssignCouponToProductRequestDTO assignCouponToProductRequestDTO, Pageable pageable) {
        if (assignCouponToProductRequestDTO.getAssignType() == AssignType.SPECIFIC_MEMBERS) {
            Page<ProductResponseForManager> pagedProductResponseForManager = productService.findProductsForManaging(
                    assignCouponToProductRequestDTO.getBrand(),
                    assignCouponToProductRequestDTO.getCategory(),
                    assignCouponToProductRequestDTO.getProductName(),
                    assignCouponToProductRequestDTO.getContent(),
                    pageable
            );

            Page<Product> pagedProducts = pagedProductResponseForManager.map(productResponse -> {
                Product product = new Product();
                product.setId(productResponse.getId());
                product.setName(productResponse.getName());
                product.setBrand(productResponse.getBrand());
                product.setCategory(
                        categoryRepository.findByCode(productResponse.getCategory()).orElseThrow(() -> new IllegalArgumentException("상위 카테고리 코드를 찾을 수 없습니다. 상위 카테고리를 먼저 생성하고 다시 시도해주세요."))
                );
                product.setProductNum(productResponse.getProductNum());
                product.setStock(productResponse.getStock());
                product.setSoldQuantity(product.getSoldQuantity());
                product.setPrice(productResponse.getPrice());
                product.setDiscountRate(productResponse.getDiscountRate());
                product.setDefectiveStock(productResponse.getDefectiveStock());
                product.setDescription(productResponse.getDescription());
                product.setCreateAt(productResponse.getCreateProductDate());
                product.setImageUrl(productResponse.getImageUrl());
                product.setProductCoupons(productResponse.getProductCoupons());
                product.setProductEvents(productResponse.getProductEvents());
                return product;
            });
            return pagedProducts;

        } else if (assignCouponToProductRequestDTO.getAssignType() == AssignType.ALL) {
            return productRepository.findAll(pageable);
        } else {
            throw new IllegalArgumentException("Invalid assign type");
        }
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


    private void sendWebSocketNotification(String userEmail, String message) {
        messagingTemplate.convertAndSendToUser(
                userEmail,
                "/queue/notifications",
                message
        );
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

    private Page<MemberCoupon> convertFromPagedMemberAndCouponToPagedMemberCoupon(Page<Member> pagedMember, Coupon coupon) {
        return pagedMember.map(member -> {
            MemberCoupon memberCoupon = new MemberCoupon();
            memberCoupon.setMember(member);
            memberCoupon.setCoupon(coupon);
            memberCoupon.setIssuedAt(LocalDateTime.now());
            memberCoupon.setUsedAt(null);
            memberCoupon.setUsed(false);

            // 양방향 관계 설정
            member.getMemberCoupons().add(memberCoupon);
            coupon.getMemberCoupons().add(memberCoupon);

            return memberCoupon;
        });
    }

    private Page<ProductCoupon> convertFromPagedProductAndCouponToPagedProductCoupon(Page<Product> pagedProduct, Coupon coupon) {
        return pagedProduct.map(product -> {
            ProductCoupon productCoupon = new ProductCoupon();
            productCoupon.setId(product.getId());
            productCoupon.setProduct(product);
            productCoupon.setCoupon(coupon);
            productCoupon.setIssuedAt(LocalDateTime.now());
            productCoupon.setUsedAt(null);
            productCoupon.setUsed(false);

            // 양방향 관계 설정
            product.getProductCoupons().add(productCoupon);
            coupon.getProductCoupons().add(productCoupon);

            return productCoupon;
        });
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
