package home.project.dto.requestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

/**
 * 새로운 상품을 생성하기 위한 요청 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 클라이언트로부터 상품 생성에 필요한 정보를 받아 서버로 전달합니다.
 */
@Getter
@Setter
public class CreateProductRequestDTO {

    /**
     * 상품의 이름입니다.
     * 이 필드는 비어있거나 null이 될 수 없습니다.
     */
    @NotBlank(message = "상품의 이름을 입력해주세요.")
    private String name;

    /**
     * 상품의 브랜드입니다.
     * 이 필드는 비어있거나 null이 될 수 없습니다.
     */
    @NotBlank(message = "상품의 브랜드를 입력해주세요.")
    private String brand;

    /**
     * 상품의 카테고리입니다.
     * 이 필드는 비어있거나 null이 될 수 없습니다.
     */
    @NotBlank(message = "상품의 카테고리를 입력해주세요.")
    private String category;

    /**
     * 상품의 현재 재고 수량입니다.
     * 이 필드는 null이 될 수 없으며, 0 이상의 값이어야 합니다.
     */
    @Check(name = "stock", constraints = "stock >= 0")
    @NotNull(message = "상품의 현재재고를 입력해주세요.")
    private Long stock;

    /**
     * 상품의 판매 수량입니다.
     * 기본값은 0입니다.
     */
    private Long soldQuantity = 0L;

    @NotBlank(message = "상품의 가격을 입력해주세요.")
    private Long price;

    @NotBlank(message = "상품의 할인율을 입력해주세요.")
    private Integer discountRate = 0;

    @NotBlank(message = "상품의 불량수량을 입력해주세요.")
    private Long defectiveStock = 0L;

    @NotBlank(message = "상품의 상세정보를 입력해주세요.")
    private String description;

    @NotBlank(message = "상품의 이미지를 입력해주세요.")
    private String imageUrl;

}