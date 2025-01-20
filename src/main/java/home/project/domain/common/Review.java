package home.project.domain.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import home.project.domain.member.Member;
import home.project.domain.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "createAt_review", columnDefinition = "TIMESTAMP")
    private LocalDateTime createAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating", nullable = false, length = 30)
    private RatingType ratingType;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_url_1", columnDefinition = "TEXT")
    private String imageUrl1;

    @Column(name = "image_url_2", columnDefinition = "TEXT")
    private String imageUrl2;

    @Column(name = "image_url_3", columnDefinition = "TEXT")
    private String imageUrl3;

    @Column(name = "helpful_count")
    private Long helpful;
}
