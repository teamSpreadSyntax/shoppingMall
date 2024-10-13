package home.project.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "shipping")
@Getter
@Setter
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shipping_type", nullable = false)
    private DeliveryType deliveryType;

    @Column(name = "delivery_num", nullable = false)
    private String deliveryNum;

    @Column(name = "arriving_date", nullable = false)
    private String arrivingDate;

    @Column(name = "arrived_date")
    private String arrivedDate = null;

    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatusType deliveryStatus;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;


}
