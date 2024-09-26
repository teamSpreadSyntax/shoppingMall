package home.project.dto.requestDTO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

@Getter
@Setter
@EqualsAndHashCode
public class UpdateProductRequestDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    @NotBlank(message = "상품의 이름을 입력해주세요.")
    private String name;

    @Column(name = "brand")
    @NotBlank(message = "상품의 브랜드를 입력해주세요.")
    private String brand;

    @Column(name = "category")
    @NotBlank(message = "상품의 카테고리를 입력해주세요.")
    private String category;

    @Column(name = "product_num")
    @NotNull(message = "상품의 품번을 입력해주세요.")
    private String productNum;

    @Check(constraints = "stock >= 0")
    @Column(name = "stock")
    @NotNull(message = "상품의 현재 재고를 입력해주세요.")
    private Long stock;

    @Check(constraints = "sold_Quantity >= 0")
    @Column(name = "sold_Quantity")
    private Long soldQuantity = 0L;

}
