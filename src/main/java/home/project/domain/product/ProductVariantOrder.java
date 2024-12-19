//package home.project.domain.product;
//
//import home.project.domain.delivery.DeliveryStatusType;
//import home.project.domain.order.Orders;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Entity
//@Table(name = "Product_variant_orders")
//@Getter
//@Setter
//public class ProductVariantOrder {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "orders_id")
//    private Orders orders;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "productVariant_id")
//    private ProductVariant productVariant;
//
//    @Column(nullable = false)
//    private Integer quantity;
//
//    @Column(nullable = false)
//    private Long price;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "delivery_status", nullable = false)
//    private DeliveryStatusType deliveryStatus;
//}
