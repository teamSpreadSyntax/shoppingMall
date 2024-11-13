package home.project.domain.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import home.project.domain.member.Member;
import home.project.domain.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "liked", nullable = false)
    private boolean liked;

    @Column(name = "createAt_wishList", columnDefinition = "TIMESTAMP")
    private LocalDateTime createAt;

}
