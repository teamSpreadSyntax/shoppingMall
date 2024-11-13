package home.project.dto.responseDTO;

import home.project.dto.requestDTO.ProductDTOForOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {

    private Long id;

    private String orderNum;

    private LocalDateTime orderDate;

    private String deliveryAddress;

    private Long totalAmount;

    private Long pointsUsed;

    private Long pointsEarned;

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
