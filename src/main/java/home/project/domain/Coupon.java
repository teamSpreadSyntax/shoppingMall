package home.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;


@Entity
@Table(name = "coupon")
@Getter
@Setter
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_name", nullable = false)
    private String name;

    @Column(name = "coupon_discountRate", nullable = false)
    private Integer discountRate;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "coupon")
    private List<ProductCoupon> productCoupons = new ArrayList<>();

    @OneToMany(mappedBy = "coupon")
    private List<MemberCoupon> memberCoupons = new ArrayList<>();
}
