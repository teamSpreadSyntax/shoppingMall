package home.project.dto.responseDTO;

import home.project.domain.Product;
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

    private Long accumulatedPurchase;

    private List<Product> products;


    public OrderResponse(Long id, String orderNum, LocalDateTime orderDate, String deliveryAddress, Long accumulatedPurchase, List<Product> products) {
        this.id = id;
        this.orderNum = orderNum;
        this.orderDate = orderDate;
        this.deliveryAddress = deliveryAddress;
        this.accumulatedPurchase = accumulatedPurchase;
        this.products = products;
    }
}
