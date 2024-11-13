package home.project.dto.requestDTO;

import home.project.domain.product.AssignType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignCouponToProductRequestDTO {
    private Long couponId;
    private AssignType assignType;
    private String brand;
    private String category;
    private String productName;
    private String content;
    private String color;
    private String size;
}
