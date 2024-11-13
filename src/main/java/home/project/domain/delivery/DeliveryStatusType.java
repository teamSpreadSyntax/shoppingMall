package home.project.domain.delivery;

import lombok.Getter;


@Getter
public enum DeliveryStatusType {
    ORDER_REQUESTED("주문요청"),
    ORDER_CANCELLATION_REQUESTED("주문취소요청"),
    ORDER_CANCELLED("주문취소"),
    ORDER_CANCELLATION_COMPLETED("주문취소완료"),
    READY_FOR_SHIPMENT("출고"),
    DELIVERY_STARTED("배송시작"),
    IN_TRANSIT("배송중"),
    DELIVERY_COMPLETED("배송완료"),
    DELIVERY_DELAYED("배송지연"),
    REFUND_REQUESTED("환불요청"),
    REFUND_IN_PROGRESS("환불"),
    REFUND_COMPLETED("환불완료"),
    RETURN_CONFIRMED("환불요청수락"),
    CHANGE_REQUESTED("교환요청"),
    CHANGE_REQUEST_CONFIRMED("교환신청수락"),
    CHANGE_COMPLETED("교환완료"),
    PURCHASE_CONFIRMED("구매확정");

    private final String description;

    DeliveryStatusType(String description) {
        this.description = description;
    }

}
