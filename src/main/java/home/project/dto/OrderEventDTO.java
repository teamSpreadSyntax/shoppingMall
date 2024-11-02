package home.project.dto;

import home.project.domain.Member;
import home.project.domain.ProductOrder;
import home.project.domain.Shipping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderEventDTO {
    private LocalDateTime orderDate;

    private Member member;

    private Shipping shipping;

    private List<ProductOrder> productOrders = new ArrayList<>();
}
