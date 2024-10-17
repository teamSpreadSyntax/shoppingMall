package home.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.*;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.requestDTO.ProductDTOForOrder;
import home.project.dto.responseDTO.OrderResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.InvalidCouponException;
import home.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
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
//    private final MemberOrderRepository memberOrderRepository;
//    private final ProductOrderRepository productOrderRepository;
    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CouponService couponService;
    private final ShippingRepository shippingRepository;
//    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Converter converter;


    @Override
    @Transactional
    public OrderResponse join(CreateOrderRequestDTO createOrderRequestDTO){

        Shipping shipping = converter.convertFromCreateOrderRequestDTOToShipping(createOrderRequestDTO);

        Orders orders = new Orders();
        LocalDateTime orderDate = LocalDateTime.now();
        orders.setOrderNum(generateOrderNumber(createOrderRequestDTO.getProductOrders(), orderDate));
        orders.setOrderDate(orderDate);
        orders.setShipping(shipping);
        shipping.setOrders(orders); // 양방향 관계 설정


        long amount = 0L;
        for (ProductDTOForOrder productDTO : createOrderRequestDTO.getProductOrders()) {
            Product product = productService.findById(productDTO.getProductId());

            productService.decreaseStock(productDTO.getProductId(), productDTO.getQuantity().longValue());
            productService.increaseSoldQuantity(productDTO.getProductId(), productDTO.getQuantity().longValue());


            ProductOrder productOrder = new ProductOrder();
            productOrder.setProduct(product);
            productOrder.setQuantity(productDTO.getQuantity());
            productOrder.setPrice(productDTO.getPrice());
            productOrder.setOrders(orders);
            orders.getProductOrders().add(productOrder);

            amount += productDTO.getPrice()*productDTO.getQuantity();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        // 3. 쿠폰 유효성 검사 및 할인 적용
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

            // 쿠폰 적용 - 할인율 계산 및 총 금액에서 차감
            Integer couponDiscountRate = coupon.getDiscountRate();
            long discountAmount = amount * couponDiscountRate / 100;
            amount -= discountAmount;

            // 쿠폰 사용 처리
            memberCoupon.setUsed(true);
            memberCoupon.setUsedAt(now);
            memberCouponRepository.save(memberCoupon); // 쿠폰 업데이트 (사용 처리)
        }

        // **총 구매 금액(amount) 저장**
        orders.setAmount(amount);  // 총 금액을 주문에 저장



        long newAccumulatePurchase = member.getAccumulatedPurchase()+amount;
        if (newAccumulatePurchase < 0) {
            member.setGrade(MemberGradeType.BRONZE);
        } else if (newAccumulatePurchase >= 10000 && newAccumulatePurchase < 200000) {
            member.setGrade(MemberGradeType.SILVER);
        } else if (newAccumulatePurchase >= 200000 && newAccumulatePurchase < 300000) {
            member.setGrade(MemberGradeType.GOLD);
        } else if (newAccumulatePurchase >= 300000) {
            member.setGrade(MemberGradeType.PLATINUM);
        }
        member.setAccumulatedPurchase(newAccumulatePurchase);
        orders.setMember(member);

        Long pointsUsed = createOrderRequestDTO.getPointsUsed();
        orders.setPointsUsed(pointsUsed);

        Long pointsEarned = (long) (orders.getAmount() * 0.05);
        orders.setPointsEarned(pointsEarned);


        memberRepository.save(member);
        orderRepository.save(orders);

        return converter.convertFromOrderToOrderResponse(orders);
    }

    private String generateOrderNumber(List<ProductDTOForOrder> orderItems, LocalDateTime orderDate) {
        String productPrefix = orderItems.stream()
                .map(item -> String.valueOf(item.getProductId()))
                .map(id -> id.length() > 0 ? id.substring(0, 1) : "")                .collect(Collectors.joining());
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
    public Page<OrderResponse> findOrders(String orderNum, String orderDate, String productNumber, String email, String content, Pageable pageable) {

        Page<Orders> pagedOrder = orderRepository.findOrders(orderNum, orderDate, productNumber, email, content, pageable);

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

        return orderNum;
    }



}
