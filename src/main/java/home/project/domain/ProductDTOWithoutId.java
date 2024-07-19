package home.project.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTOWithoutId {
    @NotBlank(message = "상품의 브랜드를 입력해주세요.")
    private String brand;

    private Long soldQuantity = 0L;

    @NotBlank(message = "상품의 이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "상품의 카테고리를 입력해주세요.")
    private String category;

    @NotNull(message = "상품의 현재재고를 입력해주세요.")
    private Long stock;

    @NotBlank(message = "상품의 이미지를 입력해주세요.")
    private String image;
}