package home.project.service;

import home.project.domain.*;
import home.project.domain.elasticsearch.MemberDocument;
import home.project.domain.elasticsearch.ProductDocument;
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

    public QnADetailResponse convertFromQnAToQnADetailResponse(QnA qnA) {
        return new QnADetailResponse(
                qnA.getId(),
                qnA.getQnAType(),
                qnA.getSubject(),
                qnA.getProduct() != null ? qnA.getProduct().getProductNum() : null,
                qnA.getOrders() != null ? qnA.getOrders().getOrderNum() : null,
                qnA.getDescription(),
                qnA.getMember().getEmail(),
                qnA.getCreateAt(),
                qnA.getAnswer(),
                qnA.getAnswerDate(),
                qnA.getAnswerer() != null ? qnA.getAnswerer().getEmail() : null,
                qnA.getAnswerStatus()
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

    public Page<QnADetailResponse> convertFromPagedQnAToPagedQnADetailResponse(Page<QnA> pagedQna) {
        return pagedQna.map(qnA -> new QnADetailResponse(
                qnA.getId(),
                qnA.getQnAType(),
                qnA.getSubject(),
                qnA.getProduct() != null ? qnA.getProduct().getProductNum() : null,
                qnA.getOrders() != null ? qnA.getOrders().getOrderNum() : null,
                qnA.getDescription(),
                qnA.getMember().getEmail(),
                qnA.getCreateAt(),
                qnA.getAnswer(),
                qnA.getAnswerDate(),
                qnA.getAnswerer() != null ? qnA.getAnswerer().getEmail() : null,
                qnA.getAnswerStatus()
        ));
    }

    public Page<QnAResponse> convertFromPagedQnAToPagedQnAResponse(Page<QnA> pagedQna) {
        return pagedQna.map(qnA -> new QnAResponse(
                qnA.getId(),
                qnA.getQnAType(),
                qnA.getSubject(),
                qnA.getMember().getEmail(),
                qnA.getCreateAt(),
                qnA.getAnswerStatus()
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
                        memberCoupon.getCoupon().getDiscountRate(),
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
                product.getSize(),
                product.getColor(),
                convertFromListedProductCouponProductCouponResponse(product.getProductCoupons())
        );
    }

    public ProductWithQnAAndReviewResponseForManager convertFromProductToProductWithQnAAndReviewResponseForManager(Product product, Page<QnA> qnAs, Page<Review> reviews){
        return new ProductWithQnAAndReviewResponseForManager(
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
                product.getSize(),
                product.getColor(),
                convertFromPagedQnAToPagedQnADetailResponse(qnAs),
                convertFromPagedReviewToPagedReviewDetailResponse(reviews),
                convertFromListedProductCouponProductCouponResponse(product.getProductCoupons())
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
                productResponseForManaging.getSize(),
                productResponseForManaging.getColor(),
                convertFromListedProductCouponProductCouponResponse(productResponseForManaging.getProductCoupons())
        ));
    }
    public Page<String> convertFromPagedProductToPagedBrand(Page<Product> pagedProduct) {
        return pagedProduct.map(Product::getBrand);
    }

    public Page<ProductResponseForManager> convertFromPagedMemberProductToPagedProductResponseForManaging(Page<MemberProduct> pagedMemberProduct){
        return pagedMemberProduct.map(productResponseForManaging -> new ProductResponseForManager(
                productResponseForManaging.getId(),
                productResponseForManaging.getProduct().getName(),
                productResponseForManaging.getProduct().getBrand(),
                productResponseForManaging.getProduct().getCategory().getCode(),
                productResponseForManaging.getProduct().getProductNum(),
                productResponseForManaging.getProduct().getStock(),
                productResponseForManaging.getProduct().getSoldQuantity(),
                productResponseForManaging.getProduct().getPrice(),
                productResponseForManaging.getProduct().getDiscountRate(),
                productResponseForManaging.getProduct().getDefectiveStock(),
                productResponseForManaging.getProduct().getDescription(),
                productResponseForManaging.getProduct().getCreateAt(),
                productResponseForManaging.getProduct().getImageUrl(),
                productResponseForManaging.getProduct().getSize(),
                productResponseForManaging.getProduct().getColor(),
                convertFromListedProductCouponProductCouponResponse(productResponseForManaging.getProduct().getProductCoupons())
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
                false,
                productResponse.getSize(),
                productResponse.getColor(),
                convertFromListedProductCouponProductCouponResponse(productResponse.getProductCoupons())
        ));
    }

    public Page<ProductResponse> convertFromPagedProductToPagedProductResponse2(Page<Product> pagedProduct,List<Long> isLiked){

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
                isLiked.contains(productResponse.getId()),
                productResponse.getSize(),
                productResponse.getColor(),
                convertFromListedProductCouponProductCouponResponse(productResponse.getProductCoupons())
        ));
    }


    public ProductResponse convertFromProductToProductResponse(Product product) {
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
                false,
                product.getSize(),
                product.getColor(),
                convertFromListedProductCouponProductCouponResponse(product.getProductCoupons())
        );
    }

    public ProductWithQnAAndReviewResponse convertFromProductToProductWithQnAAndReviewResponse(Product product, Page<QnA> qnAs, Page<Review> reviews) {
        return new ProductWithQnAAndReviewResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory().getCode(),
                product.getProductNum(),
                product.getPrice(),
                product.getDiscountRate(),
                product.getDescription(),
                product.getImageUrl(),
                false,
                product.getSize(),
                product.getColor(),
                convertFromListedProductCouponProductCouponResponse(product.getProductCoupons()),
                convertFromPagedQnAToPagedQnADetailResponse(qnAs),
                convertFromPagedReviewToPagedReviewDetailResponse(reviews)
        );
    }

    public ProductResponse convertFromProductToProductResponse2(Product product, List<Long> isLiked) {
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
                isLiked.contains(product.getId()),
                product.getSize(),
                product.getColor(),
                convertFromListedProductCouponProductCouponResponse(product.getProductCoupons())
        );
    }

    public ProductWithQnAAndReviewResponse convertFromProductToProductWithQnAAndReviewResponse2(Product product, List<Long> isLiked, Page<QnA> qnAs, Page<Review> reviews) {
        return new ProductWithQnAAndReviewResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getCategory().getCode(),
                product.getProductNum(),
                product.getPrice(),
                product.getDiscountRate(),
                product.getDescription(),
                product.getImageUrl(),
                isLiked.contains(product.getId()),
                product.getSize(),
                product.getColor(),
                convertFromListedProductCouponProductCouponResponse(product.getProductCoupons()),
                convertFromPagedQnAToPagedQnADetailResponse(qnAs),
                convertFromPagedReviewToPagedReviewDetailResponse(reviews)
        );
    }

    public Page<ReviewDetailResponse> convertFromPagedReviewToPagedReviewDetailResponse(Page<Review> pagedReview) {
        return pagedReview.map(review -> new ReviewDetailResponse(
                review.getId(),
                review.getMember().getEmail(),
                review.getProduct().getName(),
                review.getCreateAt(),
                review.getRating(),
                review.getDescription(),
                review.getImageUrl1(),
                review.getImageUrl2(),
                review.getImageUrl3(),
                review.getHelpful()
        ));
    }


    public List<QnADetailResponse> convertFromListedQnAToListedQnADetailResponse(List<QnA> qnAs) {
        if (qnAs == null) {
            return new ArrayList<>(); // null 체크
        }

        return qnAs.stream()
                .map(qnA -> new QnADetailResponse(
                        qnA.getId(),
                        qnA.getQnAType(),
                        qnA.getSubject(),
                        qnA.getProduct() != null ? qnA.getProduct().getProductNum() : null,
                        qnA.getOrders() != null ? qnA.getOrders().getOrderNum() : null,
                        qnA.getDescription(),
                        qnA.getMember().getEmail(),
                        qnA.getCreateAt(),
                        qnA.getAnswer(),
                        qnA.getAnswerDate(),
                        qnA.getAnswerer() != null ? qnA.getAnswerer().getEmail() : null,
                        qnA.getAnswerStatus()
                ))
                .collect(Collectors.toList());
    }

    // Review 리스트를 ReviewDetailResponse 리스트로 변환하는 메서드
    public List<ReviewDetailResponse> convertFromListedReviewToReviewLitedDetailResponse(List<Review> reviews) {
        if (reviews == null) {
            return new ArrayList<>(); // null 체크
        }

        return reviews.stream()
                .map(review -> new ReviewDetailResponse(
                        review.getId(),
                        review.getMember().getEmail(),
                        review.getProduct().getName(),
                        review.getCreateAt(),
                        review.getRating(),
                        review.getDescription(),
                        review.getImageUrl1(),
                        review.getImageUrl2(),
                        review.getImageUrl3(),
                        review.getHelpful()
                ))
                .collect(Collectors.toList());
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
        return pagedMember.map(member -> new MemberCouponResponse(null, member.getEmail(), coupon.getId(),coupon.getDiscountRate(), LocalDateTime.now(), null, false));
    }

    public Page<ProductCouponResponse> convertFromPagedProductAndCouponToPagedProductCouponResponse(Page<Product> pagedProduct, Coupon coupon) {
        return pagedProduct.map(product -> new ProductCouponResponse(product.getId(), product.getProductNum(), coupon.getId(), LocalDateTime.now(), null, false));
    }

    public EventResponse convertFromEventToEventResponse(Event event){
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getImage()
        );
    }

    public Page<EventResponse> convertFromPagedEventToPagedEventResponse(Page<Event> pagedEvent) {
        return pagedEvent.map(event -> new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getImage()
        ));
    }



    public Shipping convertFromCreateOrderRequestDTOToShipping(CreateOrderRequestDTO createOrderRequestDTO){

        CreateShippingRequestDTO createShippingRequestDTO = createOrderRequestDTO.getShippingInfo();

        Shipping shipping = new Shipping();
        DeliveryType deliveryType = createShippingRequestDTO.getDeliveryType();
        shipping.setDeliveryType(deliveryType);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        String defaultAddress = memberRepository.findByEmail(email).orElseThrow(() -> new IdNotFoundException(email + "(으)로 등록된 회원이 없습니다.")).getDefaultAddress();
        if(createShippingRequestDTO.getDeliveryAddressType() == DeliveryAddressType.NEW_ADDRESS){
            shipping.setDeliveryNum(createShippingRequestDTO.getDeliveryAddress().substring(0,2));
            shipping.setDeliveryAddress(createShippingRequestDTO.getDeliveryAddress());

        } else if (createShippingRequestDTO.getDeliveryAddressType() == DeliveryAddressType.DEFAULT_ADDRESS && defaultAddress != null) {
            shipping.setDeliveryNum(defaultAddress.substring(0,2));
            shipping.setDeliveryAddress(defaultAddress);

        }//예외처리 추가

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
        if (createShippingRequestDTO.getShippingMessages() == ShippingMessageType.CUSTOM) {
            shipping.setShippingMessage(createShippingRequestDTO.getCustomMessage());
        } else {
            shipping.setShippingMessage(createShippingRequestDTO.getShippingMessages().getDefaultMessage());
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
                shipping.getArrivedDate(),
                shipping.getDepartureDate(),
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
                shipping.getArrivedDate(),
                shipping.getDepartureDate(),
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

    public ReviewDetailResponse convertFromReviewToReviewDetailResponse(Review review){
        return new ReviewDetailResponse(
                review.getId(),
                review.getMember().getEmail(),
                review.getProduct().getName(),
                review.getCreateAt(),
                review.getRating(),
                review.getDescription(),
                review.getImageUrl1(),
                review.getImageUrl2(),
                review.getImageUrl3(),
                review.getHelpful()
        );
    }

    public Page<ReviewResponse> convertFromPagedReviewToPagedQnAResponse(Page<Review> pagedReview) {
        return pagedReview.map(review -> new ReviewResponse(
                review.getId(),
                review.getProduct().getName(),
                review.getMember().getEmail(),
                review.getCreateAt()
        ));
    }

    public Page<WishListDetailResponse> convertFromPagedWishListToPagedWishListResponse(Page<WishList> pagedWishList) {
        return pagedWishList.map(wishList -> new WishListDetailResponse(
                wishList.getId(),
                wishList.getProduct().getId(),
                wishList.getProduct().getName(),
                wishList.getProduct().getImageUrl(),
                wishList.getProduct().getPrice(),
                wishList.isLiked(),
                wishList.getCreateAt()
        ));
    }

    public NotificationResponse convertFromNotificationToNotificationResponse(Notification notification){
        return new NotificationResponse(
                notification.getId(),
                notification.getMember(),
                notification.getSubject(),
                notification.getDescription(),
                notification.getCreatedAt()
        );
    }

    public Page<NotificationResponse> convertFromPagedNotificationsToPagedNotificationsResponse(Page<Notification> pagedNotification){
        return pagedNotification.map(notification -> new NotificationResponse(
                notification.getId(),
                notification.getMember(),
                notification.getSubject(),
                notification.getDescription(),
                notification.getCreatedAt()
        ));
    }
    public NotificationDetailResponse convertFromNotificationToNotificationDetailResponse(Notification notification){
        return new NotificationDetailResponse(
                notification.getSubject(),
                notification.getDescription(),
                notification.getCreatedAt()
        );
    }



//    public SellerResponse convertFromSellerToSellerResponse(Seller seller) {
//        return new SellerResponse(
//                seller.getId(),
//                seller.getName(),
//                seller.getPhoneNumber(),
//                seller.getEmail(),
//                seller.getAddress()
//        );
//    }
//
//    public ProductSellerResponse convertFromProductToProductSellerResponse(Product product) {
//        Seller seller = product.getSeller();
//        return new ProductSellerResponse(
//                product.getId(),
//                product.getName(),
//                product.getBrand(),
//                product.getPrice(),
//                product.getDescription(),
//                product.getImageUrl(),
//                seller.getId(),
//                seller.getName(),
//                seller.getPhoneNumber(),
//                seller.getEmail(),
//                seller.getAddress()
//        );
//    }

    public ProductDocument convertFromProductToProductDocument(Product product) {
        ProductDocument doc = new ProductDocument();

        doc.setId(product.getId());
        doc.setName(product.getName());
        doc.setBrand(product.getBrand());
        doc.setProductNum(product.getProductNum());
        doc.setPrice(product.getPrice());
        doc.setDiscountRate(product.getDiscountRate());
        doc.setDescription(product.getDescription());
        doc.setImageUrl(product.getImageUrl());
        doc.setStock(product.getStock());
        doc.setSoldQuantity(product.getSoldQuantity());
        doc.setDefectiveStock(product.getDefectiveStock());
        doc.setCreateAt(product.getCreateAt());
        doc.setSize(product.getSize());
        doc.setColor(product.getColor());

        List<ProductDocument.ProductCoupon> productCoupons = product.getProductCoupons().stream()
                .map(pc -> {
                    ProductDocument.ProductCoupon pcDoc = new ProductDocument.ProductCoupon();
                    pcDoc.setId(pc.getId());
                    pcDoc.setIssuedAt(pc.getIssuedAt());
                    pcDoc.setUsedAt(pc.getUsedAt());
                    pcDoc.setUsed(pc.isUsed());

                    if (pc.getCoupon() != null) {
                        ProductDocument.ProductCoupon.Coupon couponDoc = new ProductDocument.ProductCoupon.Coupon();
                        couponDoc.setId(pc.getCoupon().getId());
                        couponDoc.setName(pc.getCoupon().getName());
                        couponDoc.setDiscountRate(pc.getCoupon().getDiscountRate());
                        couponDoc.setStartDate(pc.getCoupon().getStartDate());
                        couponDoc.setEndDate(pc.getCoupon().getEndDate());
                        couponDoc.setAssignBy(pc.getCoupon().getAssignBy());
                        pcDoc.setCoupon(couponDoc);
                    }
                    return pcDoc;
                })
                .collect(Collectors.toList());
        doc.setProductCoupons(productCoupons);

        return doc;
    }



    public MemberDocument convertFromMemberToMemberDocument(Member member) {
        MemberDocument doc = new MemberDocument();

        doc.setId(member.getId());
        doc.setEmail(member.getEmail());
        doc.setName(member.getName());
        doc.setPhone(member.getPhone());
        doc.setDefaultAddress(member.getDefaultAddress());
        doc.setSecondAddress(member.getSecondAddress());
        doc.setThirdAddress(member.getThirdAddress());
        doc.setRole(member.getRole().toString());
        doc.setAccumulatedPurchase(member.getAccumulatedPurchase());
        doc.setPoint(member.getPoint());
        doc.setGrade(member.getGrade().toString());
        doc.setBirthDate(member.getBirthDate().atStartOfDay());

        List<MemberDocument.MemberCoupon> memberCoupons = member.getMemberCoupons().stream()
                .map(mc -> {
                    MemberDocument.MemberCoupon mcDoc = new MemberDocument.MemberCoupon();
                    mcDoc.setId(mc.getId());
                    mcDoc.setIssuedAt(mc.getIssuedAt());
                    mcDoc.setUsedAt(mc.getUsedAt());
                    mcDoc.setUsed(mc.isUsed());

                    if (mc.getCoupon() != null) {
                        MemberDocument.MemberCoupon.Coupon couponDoc = new MemberDocument.MemberCoupon.Coupon();
                        couponDoc.setId(mc.getCoupon().getId());
                        couponDoc.setName(mc.getCoupon().getName());
                        couponDoc.setDiscountRate(mc.getCoupon().getDiscountRate());
                        couponDoc.setStartDate(mc.getCoupon().getStartDate());
                        couponDoc.setEndDate(mc.getCoupon().getEndDate());
                        couponDoc.setAssignBy(mc.getCoupon().getAssignBy());
                        mcDoc.setCoupon(couponDoc);
                    }
                    return mcDoc;
                })
                .collect(Collectors.toList());
        doc.setMemberCoupons(memberCoupons);

        return doc;
    }



}
