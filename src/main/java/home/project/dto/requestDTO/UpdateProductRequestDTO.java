package home.project.dto.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class UpdateProductRequestDTO {

    private Long id;

    @NotBlank(message = "상품의 이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "상품의 브랜드를 입력해주세요.")
    private String brand;

    private String category;

    @Check(constraints = "stock >= 0")
    @NotNull(message = "상품의 현재 재고를 입력해주세요.")
    private Long stock;

    @Check(constraints = "sold_Quantity >= 0")
    private Long soldQuantity = 0L;

    @NotNull(message = "상품의 가격을 입력해주세요.")
    private Long price;

    @NotNull(message = "상품의 할인율을 입력해주세요.")
    private Integer discountRate = 0;

    @NotNull(message = "상품의 불량수량을 입력해주세요.")
    private Long defectiveStock = 0L;

    private String size;

    private String color;
}
