package home.project.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "shippingMessage")
public class ShippingMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content; // 메시지 내용

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt; // 메시지 생성 날짜

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
