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

/**
 * 상품 정보 업데이트를 위한 요청 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 클라이언트로부터 상품 정보 수정에 필요한 데이터를 받아 서버로 전달합니다.
 */
@Getter
@Setter
@EqualsAndHashCode
public class UpdateProductRequestDTO {

    /**
     * 수정할 상품의 고유 식별자입니다.
     * 이 필드는 자동으로 생성됩니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 수정할 상품의 이름입니다.
     * 이 필드는 비어있을 수 없습니다.
     */
    @Column(name = "product_name")
    @NotBlank(message = "상품의 이름을 입력해주세요.")
    private String name;

    /**
     * 수정할 상품의 브랜드입니다.
     * 이 필드는 비어있을 수 없습니다.
     */
    @Column(name = "brand")
    @NotBlank(message = "상품의 브랜드를 입력해주세요.")
    private String brand;

    /**
     * 수정할 상품의 카테고리입니다.
     * 이 필드는 비어있을 수 없습니다.
     */
    @Column(name = "category")
    private String category;

    /**
     * 수정할 상품의 품번입니다.
     * 이 필드는 null이 될 수 없습니다.
     */
    @Column(name = "product_num")
    @NotNull(message = "상품의 품번을 입력해주세요.")
    private String productNum;

    /**
     * 수정할 상품의 재고 수량입니다.
     * 이 필드는 null이 될 수 없으며, 0 이상의 값이어야 합니다.
     */
    @Check(constraints = "stock >= 0")
    @Column(name = "stock")
    @NotNull(message = "상품의 현재 재고를 입력해주세요.")
    private Long stock;

    /**
     * 수정할 상품의 판매 수량입니다.
     * 이 필드는 0 이상의 값이어야 하며, 기본값은 0입니다.
     */
    @Check(constraints = "sold_Quantity >= 0")
    @Column(name = "sold_Quantity")
    private Long soldQuantity = 0L;

}