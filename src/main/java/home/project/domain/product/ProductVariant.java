//package home.project.domain.product;
//
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import org.hibernate.annotations.Check;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "product_variant")
//@Getter
//@Setter
//public class ProductVariant {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id")
//    private Product product;
//
//    @Column(name = "product_num")
//    private String productNum;
//
//    @JsonManagedReference
//    @OneToMany(mappedBy = "productVariant")
//    private List<ProductVariantOrder> productOrder = new ArrayList<>();
//
//    @Column(name = "size", nullable = false)
//    private String size;
//
//    @Column(name = "color", nullable = false)
//    private String color;
//
//    @Check(constraints = "stock >= 0")
//    @Column(name = "stock", nullable = false)
//    private Long stock;
//
//    @Column(name = "defective_stock")
//    private Long defectiveStock = 0L;
//
//    @Check(constraints = "sold_Quantity >= 0")
//    @Column(name = "sold_Quantity")
//    private Long soldQuantity = 0L;
//}
