package home.project.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "selledcount")
    private Long selledcount=0L;

    @Column(name = "name")
    @NotBlank(message = "상품명을 입력해주세요.")
    private String name;

    @Column(name = "category")
    @NotBlank(message = "상품카테고리를 입력해주세요.")
    private String category;

    @Column(name = "stock")
    @NotNull(message = "현재재고를 입력해주세요.")
    private Long stock;

    @Column(name = "image")
    @NotBlank(message = "이미지를 입력해주세요.")
    private String image;
}
