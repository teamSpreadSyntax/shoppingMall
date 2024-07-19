package home.project.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand")
    @NotBlank(message = "상품의 브랜드를 입력해주세요.")
    private String brand;

    @Column(name = "soldQuantity")
    private Long soldQuantity = 0L;

    @Column(name = "name")
    @NotBlank(message = "상품의 이름을 입력해주세요.")
    private String name;

    @Column(name = "category")
    @NotBlank(message = "상품의 카테고리를 입력해주세요.")
    private String category;

    @Check(name = "stock", constraints = "stock >= 0")
    @Column(name = "stock")
    @NotNull(message = "상품의 현재 재고를 입력해주세요.")
    private Long stock;

    @Column(name = "image")
    @NotBlank(message = "상품의 이미지를 입력해주세요.")
    private String image;
}
