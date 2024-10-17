package home.project.service;

import home.project.domain.*;
import home.project.dto.requestDTO.*;
import home.project.dto.responseDTO.*;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.MemberRepository;
import home.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

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

    public OrderResponse convertFromOrderToOrderResponse(Orders orders) {
        List<ProductDTOForOrder> ListedProductDTOForOrder = orders.getProductOrders().stream()
                .map(this::convertFromProductOrderToProductDTOForOrder)
                .collect(Collectors.toList());

        return new OrderResponse(
                orders.getId(),
                orders.getOrderNum(),
                orders.getOrderDate(),
                orders.getShipping().getDeliveryAddress(),
                calculateTotalAmount(orders),
                orders.getPointsUsed(),
                orders.getPointsEarned(),
                ListedProductDTOForOrder
        );
    }

    private Long calculateTotalAmount(Orders orders) {
        return orders.getProductOrders().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private ProductDTOForOrder convertFromProductOrderToProductDTOForOrder(ProductOrder orderProduct) {
        return new ProductDTOForOrder(
                orderProduct.getId(),
                orderProduct.getPrice(),
                orderProduct.getQuantity()
        );
    }

    public Page<OrderResponse> convertFromPagedOrderToPagedOrderResponse(Page<Orders> pagedOrder) {
        return pagedOrder.map(order -> new OrderResponse(
                order.getId(),
                order.getOrderNum(),
                order.getOrderDate(),
                order.getShipping().getDeliveryAddress(),
                calculateTotalAmount(order),
                order.getPointsUsed(),
                order.getPointsEarned(),
                convertListedProductOrderToProductDTOForOrder(order.getProductOrders())
        ));
    }

    private List<ProductDTOForOrder> convertListedProductOrderToProductDTOForOrder(List<ProductOrder> listedProductOrder) {
        return listedProductOrder.stream()
                .map(orderProduct -> new ProductDTOForOrder(
                        orderProduct.getProduct().getId(),
                        orderProduct.getPrice(),
                        orderProduct.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    public List<Product> convertFromListedProductOrderToListedProduct(List<ProductOrder> listedProductOrder) {
        return listedProductOrder.stream()
                .map(ProductOrder::getProduct)
                .collect(Collectors.toList());
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
                convertFromListedProductEventToListedProductEventResponse(product.getProductEvents())
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
                convertFromListedProductEventToListedProductEventResponse(productResponseForManaging.getProductEvents())
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
            return new ArrayList<>();
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


    public List<ProductEventResponse> convertFromListedProductEventToListedProductEventResponse(List<ProductEvent> listedProductEvent){
        if (listedProductEvent == null) {

            return new ArrayList<>();
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
                convertFromListedProductEventToListedProductEventResponse(event.getProductEvents()),
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

    public Shipping convertFromCreateOrderRequestDTOToShipping(CreateOrderRequestDTO createOrderRequestDTO){

        CreateShippingRequestDTO createShippingRequestDTO = createOrderRequestDTO.getShippingInfo();

        Shipping shipping = new Shipping();
        DeliveryType deliveryType = createShippingRequestDTO.getDeliveryType();
        shipping.setDeliveryType(deliveryType);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        String defaultAddress = memberRepository.findByEmail(email).orElseThrow(() -> new IdNotFoundException(email + "(으)로 등록된 회원이 없습니다.")).getDefaultAddress();
        if(createShippingRequestDTO.getDeliveryAddressType() == DeliveryAddressType.NEW_ADDRESS && defaultAddress != null){
            shipping.setDeliveryNum(createShippingRequestDTO.getDeliveryAddress().substring(0,2));
            shipping.setDeliveryAddress(createShippingRequestDTO.getDeliveryAddress());

        } else if (createShippingRequestDTO.getDeliveryAddressType() == DeliveryAddressType.DEFAULT_ADDRESS) {
            shipping.setDeliveryNum(defaultAddress.substring(0,2));
            shipping.setDeliveryAddress(defaultAddress);

        }

        if(deliveryType==DeliveryType.ORDINARY_DELIVERY){
            shipping.setArrivingDate(LocalDateTime.now().plusDays(5).toString());
            shipping.setDeliveryCost(0L);
        } else if (deliveryType==DeliveryType.STRAIGHT_DELIVERY) {
            shipping.setArrivingDate(LocalDateTime.now().plusDays(3).toString());
            shipping.setDeliveryCost(3000L);
        } else if (deliveryType==DeliveryType.REMOTE_DELIVERY) {
            shipping.setArrivingDate(LocalDateTime.now().plusDays(7).toString());
            shipping.setDeliveryCost(5000L);
        }

        return shipping;

    }


    public ShippingResponse convertFromShippingToShippingResponse(Shipping shipping){

        return new ShippingResponse(
                shipping.getId(),
                shipping.getDeliveryNum(),
                shipping.getOrders().getOrderDate(),
                shipping.getDeliveryAddress(),
                shipping.getOrders().getAmount(),
                convertListedProductOrderToProductDTOForOrder(shipping.getOrders().getProductOrders()),
                shipping.getDeliveryType(),
                shipping.getDeliveryStatus(),
                shipping.getDeliveryCost(),
                shipping.getOrders().getMember().getEmail()
        );

    }

    public Page<ShippingResponse> convertFromPagedShippingToPagedShippingResponse(Page<Shipping> pagedShipping) {
        return pagedShipping.map(shipping -> new ShippingResponse(
                shipping.getId(),
                shipping.getDeliveryNum(),
                shipping.getOrders().getOrderDate(),
                shipping.getDeliveryAddress(),
                shipping.getOrders().getAmount(),
                convertListedProductOrderToProductDTOForOrder(shipping.getOrders().getProductOrders()),
                shipping.getDeliveryType(),
                shipping.getDeliveryStatus(),
                shipping.getDeliveryCost(),
                shipping.getOrders().getMember().getEmail()
        ));
    }

    public List<ProductCart> convertFromListedProductDTOForOrderToListedProductCart(List<ProductDTOForOrder> listedProductDTOForOrder, Cart cart, Coupon coupon) {

        return listedProductDTOForOrder.stream()
                .map(productDTOForOrder -> {
                    Product product = productRepository.findById(productDTOForOrder.getProductId()).orElseThrow(() -> new IdNotFoundException(productDTOForOrder.getProductId() + "(으)로 등록된 상품이 없습니다."));
                    ProductCart productCart = new ProductCart();
                    productCart.setCart(cart);
                    productCart.setProduct(product);
                    productCart.setQuantity(productDTOForOrder.getQuantity());
                    return productCart;
                })
                .collect(Collectors.toList());
    }

    public CartResponse convertFromCartToCartResponse(Cart cart){
        return new CartResponse(
                cart.getMember().getEmail(),
                convertListedProductCartToListedProductDTOFOrOrder(cart.getProductCart())
        );
    }


    public List<ProductDTOForOrder> convertListedProductCartToListedProductDTOFOrOrder(List<ProductCart> listedProductCart){
            return listedProductCart.stream()
                    .map(productCart -> new ProductDTOForOrder(
                            productCart.getProduct().getId(),
                            productCart.getProduct().getPrice(),
                            productCart.getQuantity()
                    ))
                    .collect(Collectors.toList());
    }

    public Page<CartResponse> convertFromPagedCartToPagedCartResponse(Page<Cart> pagedCart) {
        return pagedCart.map(cart -> new CartResponse(
                cart.getMember().getEmail(),
                convertListedProductCartToListedProductDTOFOrOrder(cart.getProductCart())
        ));
    }
}
