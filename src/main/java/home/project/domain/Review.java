package home.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "review")
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "createAt_review", columnDefinition = "TIMESTAMP")
    private LocalDateTime createAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating",nullable = false, length = 30)
    private Rating rating;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String imageUrls;

    @Column(columnDefinition = "helpful_count")
    private Long count;


}
//리뷰 ID - 각 리뷰에 대한 고유 ID
//사용자 ID - 리뷰를 작성한 사용자의 고유 ID
//상품 ID - 리뷰가 작성된 특정 상품의 고유 ID
//리뷰 작성 날짜 - 리뷰가 등록된 날짜 및 시간
//별점 - 1~5 점 사이의 점수 (별점으로 평가한 경우)
//리뷰 내용 - 사용자가 남긴 리뷰 텍스트
//리뷰 이미지 - 사용자가 업로드한 이미지 링크나 경로 (있을 경우)
//추천 여부 - 다른 사용자들이 해당 리뷰를 유용하게 평가한 횟수나 추천 여부