package home.project.domain.common;

import home.project.domain.member.Member;
import home.project.domain.order.Orders;
import home.project.domain.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "qna")
@Getter
@Setter
public class QnA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "qna_type", nullable = false, length = 30)
    private QnAType qnAType = QnAType.OTHER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "subject", nullable = false)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

    @Column(name = "createAt_qna", columnDefinition = "TIMESTAMP")
    private LocalDateTime createAt;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;

    @Column(name = "answer_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime answerDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answerer_id")
    private Member answerer;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_status", length = 20)
    private AnswerStatus answerStatus = AnswerStatus.WAITING;
}
