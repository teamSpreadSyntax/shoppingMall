package home.project.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import home.project.domain.Member;
import home.project.domain.ProductOrder;
import home.project.domain.Shipping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderEventDTO {
    private String eventType;

    private LocalDateTime orderDate;

    private Member member;

    private Shipping shipping;

    private List<ProductOrder> productOrders = new ArrayList<>();

    @JsonCreator
    public OrderEventDTO(
            @JsonProperty("eventType") String eventType,
            @JsonProperty("orderDate") LocalDateTime orderDate,
            @JsonProperty("member") Member member,
            @JsonProperty("shipping") Shipping shipping,
            @JsonProperty("productOrders") List<ProductOrder> productOrders) {
        this.eventType = eventType;
        this.orderDate = orderDate;
        this.member = member;
        this.shipping = shipping;
        this.productOrders = productOrders;
    }
}
