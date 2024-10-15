package home.project.service;

import home.project.domain.*;
import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.ShippingResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.MemberRepository;
import home.project.repository.ShippingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static home.project.util.CategoryMapper.getCode;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ShippingServiceImpl implements ShippingService{

    private final ProductService productService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ShippingRepository shippingRepository;
    private final Converter converter;

    @Override
    @Transactional
    public ShippingResponse update(Long id, DeliveryStatusType deliveryStatusType){

        Shipping shipping = findById(id);
        Orders order = shipping.getOrders();
        Member member = order.getMember();

        if (deliveryStatusType == DeliveryStatusType.ORDER_CANCELLATION_COMPLETED
                || deliveryStatusType == DeliveryStatusType.REFUND_COMPLETED) {
            handleCancellationRefundOrReturn(order, member);

            restoreProductStockAndSoldQuantity(order.getProductOrders());
        }

        shipping.setDeliveryStatus(deliveryStatusType);
        shippingRepository.save(shipping);
        return converter.convertFromShippingToShippingResponse(shipping);
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
            member.setGrade(MemberGrade.BRONZE);
        } else if (accumulatedPurchase < 200000) {
            member.setGrade(MemberGrade.SILVER);
        } else if (accumulatedPurchase < 300000) {
            member.setGrade(MemberGrade.GOLD);
        } else {
            member.setGrade(MemberGrade.PLATINUM);
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

    @Override
    public Page<ShippingResponse> findAll(Pageable pageable) {
        Page<Shipping> pagedShipping = shippingRepository.findAll(pageable);
        return converter.convertFromPagedShippingToPagedShippingResponse(pagedShipping);
    }

    @Override
    public Page<ShippingResponse> findShippings(String deliveryNum, String orderDate, String orderNum, String email, String content, Pageable pageable) {

        Page<Shipping> pagedShipping = shippingRepository.findShippings(deliveryNum, orderDate, orderNum, email, content, pageable);

        return converter.convertFromPagedShippingToPagedShippingResponse(pagedShipping);
    }
}
