package home.project.domain.delivery;

import com.fasterxml.jackson.annotation.JsonBackReference;
import home.project.domain.order.Orders;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "shipping")
@Getter
@Setter
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false)
    private DeliveryType deliveryType;

    @Column(name = "delivery_num")
    private String deliveryNum = null;

    @Column(name = "delivery_Address", nullable = false)
    private String deliveryAddress;

    @Column(name = "arriving_date", nullable = false)
    private String arrivingDate;

    @Column(name = "arrived_date")
    private String arrivedDate = null;

    @Column(name = "departure_date")
    private String departureDate = null;

    @Column(name = "delivery_cost")
    private Long deliveryCost = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 30)
    private DeliveryStatusType deliveryStatus = DeliveryStatusType.ORDER_REQUESTED;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

    @Column(name = "shippingMessage")
    private String shippingMessage;


}
