package home.project.service;

import home.project.domain.*;
import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.responseDTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class Converter {

    private final PasswordEncoder passwordEncoder;

    public Member convertFromCreateMemberRequestDTOToMember(CreateMemberRequestDTO createMemberRequestDTO) {
        Member member = new Member();
        member.setEmail(createMemberRequestDTO.getEmail());
        member.setPassword(passwordEncoder.encode(createMemberRequestDTO.getPassword()));
        member.setName(createMemberRequestDTO.getName());
        member.setPhone(createMemberRequestDTO.getPhone());
        member.setGender(createMemberRequestDTO.getGender());
        member.setBirthDate(createMemberRequestDTO.getBirthDate());
        member.setDefaultAddress(createMemberRequestDTO.getDefaultAddress());
        return member;
    }

    public Page<MemberResponse> convertFromPagedMemberToPagedMemberResponse(Page<Member> pagedMember) {
        return pagedMember.map(member -> new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getRole(),
                member.getGender(),
                member.getBirthDate(),
                member.getDefaultAddress(),
                member.getSecondAddress(),
                member.getThirdAddress(),
                member.getGrade(),
                member.getPoint(),
                convertFromListedMemberCouponMemberCouponResponse(member.getMemberCoupons())
        ));
    }

    public MemberResponse convertFromMemberToMemberResponse(Member member){
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getRole(),
                member.getGender(),
                member.getBirthDate(),
                member.getDefaultAddress(),
                member.getSecondAddress(),
                member.getThirdAddress(),
                member.getGrade(),
                member.getPoint(),
                convertFromListedMemberCouponMemberCouponResponse(member.getMemberCoupons())
        );
    }

    public List<MemberCouponResponse> convertFromListedMemberCouponMemberCouponResponse(List<MemberCoupon> listedMemberCoupon){
        if (listedMemberCoupon == null) {
            return new ArrayList<>(); // 또는 null을 반환할 수 있습니다, 비즈니스 로직에 따라 결정
        }

        return listedMemberCoupon.stream()
                .map(memberCoupon -> new MemberCouponResponse(
                        memberCoupon.getId(),
                        memberCoupon.getMember().getEmail(),
                        memberCoupon.getCoupon().getId(),
                        memberCoupon.getIssuedAt(),
                        memberCoupon.getUsedAt(),
                        memberCoupon.isUsed()
                ))
                .collect(Collectors.toList());
    }

    public ProductResponseForManager convertFromProductToProductResponseForManaging(Product product){
        return new ProductResponseForManager(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory().getCode(),
                product.getProductNum(),
                product.getStock(),
                product.getSoldQuantity(),
                product.getPrice(),
                product.getDiscountRate(),
                product.getDefectiveStock(),
                product.getDescription(),
                product.getCreateAt(),
                product.getImageUrl(),
                convertFromListedProductCouponProductCouponResponse(product.getProductCoupons()),
                convertFromListedProductEventProductEventResponse(product.getProductEvents())
        );
    }

    public Page<ProductResponseForManager> convertFromPagedProductToPagedProductResponseForManaging(Page<Product> pagedProduct){
        return pagedProduct.map(productResponseForManaging -> new ProductResponseForManager(
                productResponseForManaging.getId(),
                productResponseForManaging.getName(),
                productResponseForManaging.getBrand(),
                productResponseForManaging.getCategory().getCode(),
                productResponseForManaging.getProductNum(),
                productResponseForManaging.getStock(),
                productResponseForManaging.getSoldQuantity(),
                productResponseForManaging.getPrice(),
                productResponseForManaging.getDiscountRate(),
                productResponseForManaging.getDefectiveStock(),
                productResponseForManaging.getDescription(),
                productResponseForManaging.getCreateAt(),
                productResponseForManaging.getImageUrl(),
                convertFromListedProductCouponProductCouponResponse(productResponseForManaging.getProductCoupons()),
                convertFromListedProductEventProductEventResponse(productResponseForManaging.getProductEvents())
        ));
    }

    public Page<ProductResponse> convertFromPagedProductToPagedProductResponse(Page<Product> pagedProduct){
        return pagedProduct.map(productResponse -> new ProductResponse(
                productResponse.getId(),
                productResponse.getName(),
                productResponse.getBrand(),
                productResponse.getCategory().getCode(),
                productResponse.getProductNum(),
                productResponse.getPrice(),
                productResponse.getDiscountRate(),
                productResponse.getDescription(),
                productResponse.getImageUrl(),
                convertFromListedProductCouponProductCouponResponse(productResponse.getProductCoupons())
        ));
    }

    public ProductResponse convertFromProductToProductResponse(Product product){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory().getCode(),
                product.getProductNum(),
                product.getPrice(),
                product.getDiscountRate(),
                product.getDescription(),
                product.getImageUrl(),
                convertFromListedProductCouponProductCouponResponse(product.getProductCoupons())
        );
    }

    public List<ProductCouponResponse> convertFromListedProductCouponProductCouponResponse(List<ProductCoupon> listedProductCoupon){
        if (listedProductCoupon == null) {
            return new ArrayList<>(); // 또는 null을 반환할 수 있습니다, 비즈니스 로직에 따라 결정
        }

        return listedProductCoupon.stream()
                .map(productCoupon -> new ProductCouponResponse(
                        productCoupon.getId(),
                        productCoupon.getProduct().getProductNum(),
                        productCoupon.getCoupon().getId(),
                        productCoupon.getIssuedAt(),
                        productCoupon.getUsedAt(),
                        productCoupon.isUsed()
                ))
                .collect(Collectors.toList());
    }


    public List<ProductEventResponse> convertFromListedProductEventProductEventResponse(List<ProductEvent> listedProductEvent){
        if (listedProductEvent == null) {

            return new ArrayList<>(); // 또는 null을 반환할 수 있습니다, 비즈니스 로직에 따라 결정
        }

        return listedProductEvent.stream()
                .map(productEvent -> new ProductEventResponse(
                        productEvent.getId(),
                        productEvent.getProduct().getProductNum(),
                        productEvent.getEvent().getId(),
                        productEvent.getProduct().getCreateAt()
                ))
                .collect(Collectors.toList());
    }



    public CouponResponse convertFromCouponToCouponResponse(Coupon coupon){
        return new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountRate(),
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getAssignBy(),
                convertFromListedProductCouponProductCouponResponse(coupon.getProductCoupons()),
                convertFromListedMemberCouponMemberCouponResponse(coupon.getMemberCoupons())
        );
    }

    public Page<CouponResponse> convertFromPagedCouponToPagedCouponResponse(Page<Coupon> pagedCoupon) {
        return pagedCoupon.map(coupon -> new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountRate(),
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getAssignBy(),
                convertFromListedProductCouponProductCouponResponse(coupon.getProductCoupons()),
                convertFromListedMemberCouponMemberCouponResponse(coupon.getMemberCoupons())
        ));
    }

        public Page<MemberCouponResponse> convertFromPagedMemberAndCouponToPagedMemberCouponResponse(Page<Member> pagedMember, Coupon coupon) {
        return pagedMember.map(member -> new MemberCouponResponse(null, member.getEmail(), coupon.getId(), LocalDateTime.now(), null, false));
    }

    public Page<ProductCouponResponse> convertFromPagedProductAndCouponToPagedProductCouponResponse(Page<Product> pagedProduct, Coupon coupon) {
        return pagedProduct.map(product -> new ProductCouponResponse(product.getId(), product.getProductNum(), coupon.getId(), LocalDateTime.now(), null, false));
    }

    public EventResponse convertFromEventToEventResponse(Event event){
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDiscountRate(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getImage(),
                convertFromListedProductEventProductEventResponse(event.getProductEvents()),
                convertFromListedMemberEventToMemberEventResponse(event.getMemberEvents())
        );
    }

    public Page<EventResponse> convertFromPagedEventToPagedEventResponse(Page<Event> pagedEvent) {
        return pagedEvent.map(event -> new EventResponse(
                event.getId(),
                event.getName(),
                event.getDiscountRate(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getImage(),
                convertFromListedProductEventToProductEventResponse(event.getProductEvents()),
                convertFromListedMemberEventToMemberEventResponse(event.getMemberEvents())
        ));
    }

    public List<ProductEventResponse> convertFromListedProductEventToProductEventResponse(List<ProductEvent> listedProductEvent){
        if (listedProductEvent == null) {

            return new ArrayList<>(); // 또는 null을 반환할 수 있습니다, 비즈니스 로직에 따라 결정
        }

        return listedProductEvent.stream()
                .map(productEvent -> new ProductEventResponse(
                        productEvent.getId(),
                        productEvent.getProduct().getProductNum(),
                        productEvent.getEvent().getId(),
                        productEvent.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public List<MemberEventResponse> convertFromListedMemberEventToMemberEventResponse(List<MemberEvent> listedMemberEvent){
        if (listedMemberEvent == null) {
            return new ArrayList<>(); // 또는 null을 반환할 수 있습니다, 비즈니스 로직에 따라 결정
        }

        return listedMemberEvent.stream()
                .map(memberEvent -> new MemberEventResponse(
                        memberEvent.getId(),
                        memberEvent.getMember().getEmail(),
                        memberEvent.getEvent().getId(),
                        memberEvent.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
