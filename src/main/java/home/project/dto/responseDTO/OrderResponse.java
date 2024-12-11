package home.project.dto.responseDTO;

import home.project.dto.requestDTO.ProductDTOForOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "주문 응답")
public class OrderResponse {

    @Schema(description = "주문 ID", example = "1")
    private Long id;

    @Schema(description = "주문 번호", example = "ORD-2024-0001")
    private String orderNum;

    @Schema(description = "주문 날짜")
    private LocalDateTime orderDate;

    @Schema(description = "배송 주소", example = "서울특별시 강남구 테헤란로")
    private String deliveryAddress;

    @Schema(description = "총 금액", example = "50000")
    private Long totalAmount;

    @Schema(description = "사용 포인트", example = "500")
    private Long pointsUsed;

    @Schema(description = "적립 포인트", example = "100")
    private Long pointsEarned;

    @Schema(description = "주문한 상품 목록")
    private List<ProductDTOForOrder> products;

    public OrderResponse(Long id, String orderNum, LocalDateTime orderDate, String deliveryAddress, Long totalAmount, Long pointsUsed, Long pointsEarned, List<ProductDTOForOrder> products) {
        this.id = id;
        this.orderNum = orderNum;
        this.orderDate = orderDate;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.pointsUsed = pointsUsed;
        this.pointsEarned = pointsEarned;
        this.products = products;
    }
}
