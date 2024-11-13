package home.project.domain.member;

import com.fasterxml.jackson.annotation.JsonBackReference;
import home.project.domain.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "member_product")
@Getter
@Setter
public class MemberProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
