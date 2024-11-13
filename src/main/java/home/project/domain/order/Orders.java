package home.project.domain.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import home.project.domain.delivery.Shipping;
import home.project.domain.member.Member;
import home.project.domain.product.ProductOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "orders")
@Getter
@Setter
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_num", nullable = false)
    private String orderNum;

    @Column(name = "delivery_date", nullable = false)
    private LocalDateTime orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonBackReference
    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Shipping shipping;

    @Column(name = "amount", nullable = false)
    Long amount;

    @Column(name = "points_used")
    private Long pointsUsed = 0L;

    // 주문으로 적립된 포인트
    @Column(name = "points_earned")
    private Long pointsEarned = 0L;

    @JsonBackReference
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOrder> productOrders = new ArrayList<>();

//
//    @JsonManagedReference
//    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<MemberCoupon> memberCoupons = new ArrayList<>();
}
