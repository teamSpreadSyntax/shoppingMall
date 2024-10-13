package home.project.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "order")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_num", nullable = false)
    private String orderNum;

    @Column(name = "delivery_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "coupon_discount_rate", nullable = false)
    private String deliveryAddress;

    @Column(name = "accumulated_purchase", nullable = false)
    private Long accumulatedPurchase;

    @Column(name = "product_number")
    private String productNumber;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Shipping shipping;
//
//    @JsonManagedReference
//    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<MemberCoupon> memberCoupons = new ArrayList<>();
}
