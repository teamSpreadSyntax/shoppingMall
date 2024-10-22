//package home.project.domain;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//
//
//@Entity
//@Table(name = "qna")
//@Getter
//@Setter
//public class Review {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "qna_type", nullable = false, length = 30)
//    private QnAType qnAType = QnAType.OTHER;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;
//
//    @Column(name = "subject", nullable = false)
//    private String subject;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id")
//    private Product product;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "starRating")
//    private StarRatingType starRatingType = StarRatingType.FIVE ;
//
//    @Column(name = "createAt_qna", columnDefinition = "TIMESTAMP")
//    private LocalDateTime createAt;
//
//    @Column(name = "description", nullable = false)
//    private String description;
//
//}
