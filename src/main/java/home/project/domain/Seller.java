//package home.project.domain;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@Table(name = "seller")
//public class Seller {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column(nullable = false)
//    private String phoneNumber;
//
//    @Column(nullable = false)
//    private String email;
//
//    private String address;
//
//    @OneToOne
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;
//
//    @JsonBackReference
//    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Product> products = new ArrayList<>();
//}
