package home.project.service.order;

import home.project.domain.delivery.DeliveryStatusType;
import home.project.domain.delivery.Shipping;
import home.project.domain.member.Member;
import home.project.domain.member.MemberGradeType;
import home.project.domain.order.Orders;
import home.project.domain.product.Product;
import home.project.domain.product.ProductOrder;
import home.project.dto.responseDTO.ShippingResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.member.MemberRepository;
import home.project.repository.order.OrderRepository;
import home.project.repository.order.ProductOrderRepository;
import home.project.repository.shipping.ShippingRepository;
import home.project.service.member.MemberService;
import home.project.service.product.ProductService;
import home.project.service.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ShippingServiceImpl implements ShippingService{

    private final ProductService productService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ShippingRepository shippingRepository;
    private final Converter converter;
    private final OrderRepository orderRepository;
    private final ProductOrderRepository productOrderRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    public ShippingResponse update(Long id, DeliveryStatusType deliveryStatusType) {

        Shipping shipping = findById(id);
        Orders order = shipping.getOrders();
        Member member = order.getMember();

        if (deliveryStatusType == DeliveryStatusType.ORDER_CANCELLATION_COMPLETED
                || deliveryStatusType == DeliveryStatusType.REFUND_COMPLETED) {
            handleCancellationRefundOrReturn(order, member);
        }

        if (deliveryStatusType == DeliveryStatusType.DELIVERY_STARTED) {
            shipping.setDeliveryNum(generateShippingNumber());
            shipping.setDepartureDate(LocalDateTime.now().toString()); // 출발일 설정
        }

        if (deliveryStatusType == DeliveryStatusType.DELIVERY_COMPLETED) {
            if (shipping.getDepartureDate() != null && !shipping.getDepartureDate().isEmpty()) {
                shipping.setArrivedDate(LocalDateTime.now().toString());
            } else {
                throw new IllegalStateException("출발일이 설정되지 않아 도착일을 설정할 수 없습니다.");
            }
        }

        for (ProductOrder productOrder : order.getProductOrders()) {
            productOrder.setDeliveryStatus(deliveryStatusType);
        }

        shipping.setDeliveryStatus(deliveryStatusType);
        shippingRepository.save(shipping);
        productOrderRepository.saveAll(order.getProductOrders());
        return converter.convertFromShippingToShippingResponse(shipping);
    }


    private String generateShippingNumber() {
        String timePart = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        return "SHIP" + timePart + randomPart;
    }

    private void handleCancellationRefundOrReturn(Orders order, Member member) {
        long orderAmount = order.getAmount();
        updateMemberPurchaseAndGrade(member, -orderAmount);
        restoreProductStockAndSoldQuantity(order.getProductOrders());
        updateMemberPoints(member, order);
        memberRepository.save(member);
    }

    private void updateMemberPurchaseAndGrade(Member member, long amountChange) {
        long newAccumulatedPurchase = Math.max(member.getAccumulatedPurchase() + amountChange, 0);
        member.setAccumulatedPurchase(newAccumulatedPurchase);
        updateMemberGrade(member, newAccumulatedPurchase);
    }

    private void updateMemberGrade(Member member, long accumulatedPurchase) {
        if (accumulatedPurchase < 10000) {
            member.setGrade(MemberGradeType.BRONZE);
        } else if (accumulatedPurchase < 200000) {
            member.setGrade(MemberGradeType.SILVER);
        } else if (accumulatedPurchase < 300000) {
            member.setGrade(MemberGradeType.GOLD);
        } else {
            member.setGrade(MemberGradeType.PLATINUM);
        }
    }

    private void updateMemberPoints(Member member, Orders order) {
        long pointsUsed = order.getPointsUsed();
        long pointsEarned = order.getPointsEarned();

        member.setPoint(member.getPoint() + pointsUsed);

        member.setPoint(member.getPoint() - pointsEarned);

        if (member.getPoint() < 0) {
            member.setPoint(0L);
        }
    }

    private void restoreProductStockAndSoldQuantity(List<ProductOrder> productOrders) {
        for (ProductOrder productOrder : productOrders) {
            Product product = productOrder.getProduct();
            int quantity = productOrder.getQuantity();
            productService.increaseStock(product.getId(), (long) quantity);
            productService.decreaseSoldQuantity(product.getId(), (long) quantity);
        }
    }

    @Override
    public Shipping findById(Long shippingId){
        return shippingRepository.findById(shippingId)
                .orElseThrow(() -> new IdNotFoundException(shippingId + "(으)로 등록된 배송정보가 없습니다."));
    }

    @Override
    public ShippingResponse findByIdReturnShippingResponse(Long shippingId) {
        Shipping shipping = findById(shippingId);
        return converter.convertFromShippingToShippingResponse(shipping);
    }

//    @Override
//    public Page<ShippingResponse> findByMemberIdReturnShippingResponse(Long shippingId, Pageable pageable) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//
//        Long memberId = memberRepository.findByEmail(email).orElseThrow(() -> new IdNotFoundException(email + "(으)로 등록된 회원이 없습니다.")).getId();
//
//        Page<Shipping> shipping = shippingRepository.findByMemberId(memberId, pageable);
//        return converter.convertFromPagedShippingToPagedShippingResponse(shipping);
//    }

    @Override
    public Page<ShippingResponse> findAll(Pageable pageable) {
        Page<Shipping> pagedShipping = shippingRepository.findAll(pageable);
        return converter.convertFromPagedShippingToPagedShippingResponse(pagedShipping);
    }

    @Override
    public Page<ShippingResponse> findShippings(String deliveryNum, String orderDate, String productNum, String email, String content, Pageable pageable) {

        Page<Shipping> pagedShipping = shippingRepository.findShippings(deliveryNum, orderDate, productNum, email, content, pageable);

        return converter.convertFromPagedShippingToPagedShippingResponse(pagedShipping);
    }

}
