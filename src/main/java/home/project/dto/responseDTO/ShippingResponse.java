package home.project.dto.responseDTO;

import home.project.domain.delivery.DeliveryStatusType;
import home.project.domain.delivery.DeliveryType;
import home.project.dto.requestDTO.ProductDTOForOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "배송 응답")
public class ShippingResponse {

    @Schema(description = "배송 ID", example = "1")
    private Long id;

    @Schema(description = "배송 번호", example = "DEL-2024-0001")
    private String deliveryNum;

    @Schema(description = "주문 날짜")
    private LocalDateTime orderDate;

    @Schema(description = "배송 주소", example = "서울특별시 강남구 테헤란로")
    private String deliveryAddress;

    @Schema(description = "총 금액", example = "100000")
    private Long totalAmount;

    @Schema(description = "배송 상품 목록")
    private List<ProductDTOForOrder> products;

    @Schema(description = "배송 유형", example = "EXPRESS")
    private DeliveryType deliveryType;

    @Schema(description = "도착 날짜", example = "2024-12-31")
    private String arrivedDate;

    @Schema(description = "출발 날짜", example = "2024-12-01")
    private String departureDate;

    @Schema(description = "배송 상태", example = "IN_TRANSIT")
    private DeliveryStatusType deliveryStatusType;

    @Schema(description = "배송비", example = "3000")
    private Long deliveryCost;

    @Schema(description = "회원 이메일", example = "user@example.com")
    private String memberEmail;

    public ShippingResponse(Long id, String deliveryNum, LocalDateTime orderDate, String deliveryAddress, Long totalAmount, List<ProductDTOForOrder> products, DeliveryType deliveryType, String arrivedDate, String departureDate, DeliveryStatusType deliveryStatusType, Long deliveryCost, String memberEmail) {
        this.id = id;
        this.deliveryNum = deliveryNum;
        this.orderDate = orderDate;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.products = products;
        this.deliveryType = deliveryType;
        this.arrivedDate = arrivedDate;
        this.departureDate = departureDate;
        this.deliveryStatusType = deliveryStatusType;
        this.deliveryCost = deliveryCost;
        this.memberEmail = memberEmail;
    }
}
