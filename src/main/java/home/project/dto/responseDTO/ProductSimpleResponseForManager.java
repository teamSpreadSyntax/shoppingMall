package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "관리자용 상품 간단 응답")
public class ProductSimpleResponseForManager {

    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품 이름", example = "블루 데님 자켓")
    private String name;

    @Schema(description = "브랜드 이름", example = "리바이스")
    private String brand;

    @Schema(description = "재고 수량", example = "100")
    @Check(constraints = "stock >= 0")
    private Long stock;

    @Schema(description = "판매 수량", example = "50")
    @Check(constraints = "sold_Quantity >= 0")
    private Long soldQuantity;

    @Schema(description = "가격", example = "89000")
    private Long price;

    @Schema(description = "할인율", example = "20")
    private Integer discountRate;

    @Schema(description = "생성일")
    private LocalDateTime createProductDate;

    @Schema(description = "사이즈", example = "M")
    private String size;

    @Schema(description = "색상", example = "블루")
    private String color;

    public ProductSimpleResponseForManager(Long id, String name, String brand, Long stock, Long soldQuantity, Long price, Integer discountRate, LocalDateTime createProductDate, String size, String color) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.stock = stock;
        this.soldQuantity = soldQuantity;
        this.price = price;
        this.discountRate = discountRate;
        this.createProductDate = createProductDate;
        this.size = size;
        this.color = color;
    }
}
