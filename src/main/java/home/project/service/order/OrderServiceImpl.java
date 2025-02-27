package home.project.service.order;

import home.project.domain.delivery.DeliveryStatusType;
import home.project.domain.delivery.Shipping;
import home.project.domain.elasticsearch.MemberDocument;
import home.project.domain.elasticsearch.OrdersDocument;
import home.project.domain.member.Member;
import home.project.domain.member.MemberGradeType;
import home.project.domain.order.Orders;
import home.project.domain.product.Coupon;
import home.project.domain.product.MemberCoupon;
import home.project.domain.product.Product;
import home.project.domain.product.ProductOrder;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.requestDTO.ProductDTOForOrder;
import home.project.dto.responseDTO.OrderResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.InvalidCouponException;
import home.project.repository.member.MemberRepository;
import home.project.repository.order.OrderRepository;
import home.project.repository.order.ProductOrderRepository;
import home.project.repository.promotion.MemberCouponRepository;
import home.project.repositoryForElasticsearch.OrdersElasticsearchRepository;
import home.project.service.member.MemberService;
import home.project.service.product.ProductService;
import home.project.service.promotion.CouponService;
import home.project.service.util.Converter;
import home.project.service.integration.IndexToElasticsearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CouponService couponService;
    private final Converter converter;
    private final IndexToElasticsearch indexToElasticsearch;
    private final OrdersElasticsearchRepository ordersElasticsearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductOrderRepository productOrderRepository;


    @Override
    @Transactional
    public OrderResponse join(CreateOrderRequestDTO createOrderRequestDTO){
        // 1. 주문 기본 정보 생성
        Orders orders = createOrderBasicInfo(createOrderRequestDTO);

        // 2. 상품 정보 처리 및 주문 금액 계산
        long amount = processProductOrders(createOrderRequestDTO, orders);

        // 3. 회원 정보 조회
        Member member = getCurrentMember();

        // 4. 쿠폰 적용
        amount = applyCoupon(createOrderRequestDTO, member, amount);
        orders.setAmount(amount);

        // 5. 회원 등급 업데이트
        updateMemberGrade(member, amount);
        orders.setMember(member);

        // 6. 포인트 처리
        processPoints(createOrderRequestDTO, orders, member);

        // 7. 데이터 저장
        saveOrderData(member, orders);

        // 8. 검색 인덱싱
        indexOrderData(orders, member);

        return converter.convertFromOrderToOrderResponse(orders);
    }

    // 1. 주문 기본 정보 생성
    private Orders createOrderBasicInfo(CreateOrderRequestDTO createOrderRequestDTO) {
        // DTO에서 배송 정보 추출 및 변환
        Shipping shipping = converter.convertFromCreateOrderRequestDTOToShipping(createOrderRequestDTO);

        // 주문 객체 초기화 및 기본 정보 설정
        Orders orders = new Orders();
        LocalDateTime orderDate = LocalDateTime.now();
        orders.setOrderNum(generateOrderNumber(createOrderRequestDTO.getProductOrders(), orderDate));
        orders.setOrderDate(orderDate);
        orders.setShipping(shipping);
        shipping.setOrders(orders); // 양방향 관계 설정

        return orders;
    }

    // 2. 상품 정보 처리 및 주문 금액 계산
    private long processProductOrders(CreateOrderRequestDTO createOrderRequestDTO, Orders orders) {
        long amount = 0L;
        // 주문에 포함된 모든 상품 처리
        for (ProductDTOForOrder productDTO : createOrderRequestDTO.getProductOrders()) {
            // 상품 정보 조회
            Product product = productService.findById(productDTO.getProductId());

            // 재고 감소 및 판매량 증가 처리
            productService.decreaseStock(productDTO.getProductId(), productDTO.getQuantity().longValue());
            productService.increaseSoldQuantity(productDTO.getProductId(), productDTO.getQuantity().longValue());

            // 상품주문을 위한 엔티티 생성 및 설정
            ProductOrder productOrder = new ProductOrder();
            productOrder.setProduct(product);
            productOrder.setQuantity(productDTO.getQuantity());
            productOrder.setPrice(productDTO.getPrice());
            productOrder.setOrders(orders);
            productOrder.setDeliveryStatus(DeliveryStatusType.ORDER_REQUESTED); // 초기 배송 상태 설정
            orders.getProductOrders().add(productOrder); // 양방향 연관관계 설정

            // 주문 금액 누적 계산
            amount += productDTO.getPrice() * productDTO.getQuantity();
        }
        return amount;
    }

    // 3. 회원 정보 조회
    private Member getCurrentMember() {
        // 시큐리티 컨텍스트에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        // 이메일로 회원 정보 조회(현재 로그인한 회원 엔티티)및 반환
        return memberService.findByEmail(email);
    }

    // 4. 쿠폰 적용
    private long applyCoupon(CreateOrderRequestDTO createOrderRequestDTO, Member member, long amount) {
        // 쿠폰 정보 조회
        if (createOrderRequestDTO.getCouponId() != null) { // 쿠폰이 있을 경우
            Coupon coupon = couponService.findById(createOrderRequestDTO.getCouponId());

            // 현재 날짜를 기준으로 유효기간 검사
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
                throw new InvalidCouponException("쿠폰이 유효하지 않습니다. 유효기간을 확인해 주세요.");
            }

            // 이미 사용된 쿠폰인지 확인
            MemberCoupon memberCoupon = memberCouponRepository.findByMemberAndCoupon(member, coupon)
                    .orElseThrow(() -> new InvalidCouponException("해당 회원에게 할당된 쿠폰이 아닙니다."));

            if (memberCoupon.isUsed()) {
                throw new InvalidCouponException("이미 사용된 쿠폰입니다.");
            }

            // 할인 금액 계산 및 적용
            Integer couponDiscountRate = coupon.getDiscountRate();
            long discountAmount = amount * couponDiscountRate / 100;
            amount -= discountAmount;

            // 쿠폰 사용 처리
            memberCoupon.setUsed(true);
            memberCoupon.setUsedAt(now);
            memberCouponRepository.save(memberCoupon);
        }
        return amount;
    }

    // 5. 회원 등급 업데이트
    private void updateMemberGrade(Member member, long amount) {
        // 누적 구매액 계산
        long newAccumulatePurchase = member.getAccumulatedPurchase() + amount;

        // 누적 구매액에 따른 등급 재설정
        if (newAccumulatePurchase < 0) {
            member.setGrade(MemberGradeType.BRONZE);
        } else if (newAccumulatePurchase >= 10000 && newAccumulatePurchase < 200000) {
            member.setGrade(MemberGradeType.SILVER);
        } else if (newAccumulatePurchase >= 200000 && newAccumulatePurchase < 300000) {
            member.setGrade(MemberGradeType.GOLD);
        } else if (newAccumulatePurchase >= 300000) {
            member.setGrade(MemberGradeType.PLATINUM);
        }
        // 누적 구매액 갱신
        member.setAccumulatedPurchase(newAccumulatePurchase);
    }

    // 6. 포인트 처리
    private void processPoints(CreateOrderRequestDTO createOrderRequestDTO, Orders orders, Member member) {
        // 사용 포인트 가져오기
        Long pointsUsed = createOrderRequestDTO.getPointsUsed();

        // 회원이 가진 포인트보다 많이 사용할 경우 예외 처리
        if (pointsUsed > member.getPoint()) {
            throw new IllegalArgumentException("사용 가능한 포인트를 초과하였습니다.");
        }

        // 사용된 포인트를 주문 객체에 저장
        orders.setPointsUsed(pointsUsed);

        // 적립 포인트 계산 (5%)
        Long pointsEarned = (long) (orders.getAmount() * 0.05);
        orders.setPointsEarned(pointsEarned);

        // 포인트 차감 & 적립
        member.setPoint(member.getPoint() - pointsUsed + pointsEarned);
    }

    // 7. 데이터 저장
    private void saveOrderData(Member member, Orders orders) {
        // 회원 정보 저장 (등급, 포인트 변경사항 반영)
        memberRepository.save(member);
        // 주문 정보 저장
        orderRepository.save(orders);
    }

    // 8. 검색 인덱싱
    private void indexOrderData(Orders orders, Member member) {
        // 주문 정보 Elasticsearch 인덱싱
        OrdersDocument ordersDocument = converter.convertFromOrderToOrdersDocument(orders);
        try {
            indexToElasticsearch.indexDocumentToElasticsearch(ordersDocument, OrdersDocument.class);
        } catch (Exception e) {
            System.out.println("엘라스틱서치 인덱스 생성중 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }

        // 회원 정보 Elasticsearch 인덱싱
        MemberDocument memberDocument = converter.convertFromMemberToMemberDocument(member);
        try {
            indexToElasticsearch.indexDocumentToElasticsearch(memberDocument, MemberDocument.class);
        } catch (Exception e) {
            System.out.println("엘라스틱서치 인덱스 생성중 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateOrderNumber(List<ProductDTOForOrder> orderItems, LocalDateTime orderDate) {
        String productPrefix = orderItems.stream()
                .map(item -> String.valueOf(item.getProductId()))
                .map(id -> id.length() > 0 ? id.substring(0, 1) : "")
                .collect(Collectors.joining());
        String orderDateString = orderDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return productPrefix + orderDateString;
    }

    @Override
    public Page<OrderResponse> findAll(Pageable pageable) {
        Page<Orders> pagedOrder= orderRepository.findAll(pageable);
        return converter.convertFromPagedOrderToPagedOrderResponse(pagedOrder);
    }

    @Override
    public OrderResponse findByIdReturnOrderResponse(Long orderId) {
        return converter.convertFromOrderToOrderResponse(findById(orderId));
    }

    @Override
    public Orders findById(Long orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IdNotFoundException(orderId + "(으)로 등록된 주문이 없습니다."));
    }

    @Override
    public Page<OrderResponse> findByMemberId(Pageable pageable){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Long memberId = memberRepository.findByEmail(email).orElseThrow(() -> new IdNotFoundException(email + "(으)로 등록된 회원이 없습니다.")).getId();

        return converter.convertFromPagedOrderToPagedOrderResponse(orderRepository.findByMemberId(memberId, pageable));
    }

    @Override
    public Orders findByOrderNum(String OrderNum){
        return orderRepository.findByOrderNum(OrderNum)
                .orElseThrow(() -> new IdNotFoundException(OrderNum + "(으)로 등록된 주문이 없습니다."));
    }

    @Override
    public Page<OrderResponse> findOrders(String orderNum, String orderDate, String productNumber, String email, String content, Pageable pageable) {
        // Elasticsearch 검색 수행
        Page<OrdersDocument> pagedDocuments = ordersElasticsearchRepository.findOrders(orderNum, orderDate, productNumber, email, content, pageable);

        // OrdersDocument를 Orders 엔티티로 변환
        Page<Orders> pagedOrder = pagedDocuments.map(ordersDocument -> findById(ordersDocument.getId()));

        // Orders를 OrderResponse로 변환
        return converter.convertFromPagedOrderToPagedOrderResponse(pagedOrder);
    }

    @Override
    @Transactional
    public String deleteById(Long orderId) {
        Orders order = findById(orderId);
        String orderNum = order.getOrderNum();

        Member member = order.getMember();
        long orderAmount = order.getAmount();

        long newAccumulatedPurchase = Math.max(member.getAccumulatedPurchase() - orderAmount, 0);
        member.setAccumulatedPurchase(newAccumulatedPurchase);

        if (newAccumulatedPurchase < 10000) {
            member.setGrade(MemberGradeType.BRONZE);
        } else if (newAccumulatedPurchase < 200000) {
            member.setGrade(MemberGradeType.SILVER);
        } else if (newAccumulatedPurchase < 300000) {
            member.setGrade(MemberGradeType.GOLD);
        } else {
            member.setGrade(MemberGradeType.PLATINUM);
        }

        for (ProductOrder productOrder : order.getProductOrders()) {
            Product product = productOrder.getProduct();
            int quantity = productOrder.getQuantity();
            productService.increaseStock(product.getId(), (long) quantity);
            productService.decreaseSoldQuantity(product.getId(), (long) quantity);

        }

        orderRepository.deleteById(orderId);
        memberRepository.save(member);

        elasticsearchOperations.delete(String.valueOf(orderId), OrdersDocument.class);

        return orderNum;
    }
    @Override
    @Transactional
    public void confirmPurchase(Long orderId) {

        Orders order = findById(orderId);

        for (ProductOrder productOrder : order.getProductOrders()) {
            if (productOrder.getDeliveryStatus() == DeliveryStatusType.DELIVERY_COMPLETED) {
                productOrder.setDeliveryStatus(DeliveryStatusType.PURCHASE_CONFIRMED);
//                productOrderRepository.save(productOrder);
            }else {
                throw new IllegalStateException("배송이 완료된 상품만 구매 확정이 가능합니다.");
            }
        }
        OrdersDocument ordersDocument = converter.convertFromOrderToOrdersDocument(order);
        try {
            indexToElasticsearch.indexDocumentToElasticsearch(ordersDocument, OrdersDocument.class);
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
            e.printStackTrace();
        }

        orderRepository.save(order);
    }


}
