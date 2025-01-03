package home.project.dto.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CreateProductRequestDTO {

    @NotBlank(message = "상품의 이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "상품의 브랜드를 입력해주세요.")
    private String brand;

    @NotBlank(message = "상품의 카테고리를 입력해주세요.")
    private String category;

    @NotNull(message = "상품의 현재 재고를 입력해주세요.")
    private Long stock;

    private Long soldQuantity = 0L;

    @NotNull(message = "상품의 가격을 입력해주세요.")
    private Long price;

    @NotNull(message = "상품의 할인율을 입력해주세요.")
    private Integer discountRate = 0;

    @NotNull(message = "상품의 불량 수량을 입력해주세요.")
    private Long defectiveStock = 0L;


    @NotBlank(message = "상품의 사이즈를 입력해주세요.")
    private String size;

    @NotBlank(message = "상품의 색깔을 입력해주세요.")
    private String color;
}