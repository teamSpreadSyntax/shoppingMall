package home.project.domain.promotion;

import com.fasterxml.jackson.annotation.JsonBackReference;
import home.project.domain.product.Coupon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "event_coupon")
@Getter
@Setter
public class EventCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "is_used")
    private boolean isUsed = false;
}
