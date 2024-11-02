package home.project.dto.requestDTO;

import home.project.domain.AssignType;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AssignCouponToProductRequestDTO {
    private Long couponId;
    private AssignType assignType;
    private String brand;
    private String category;
    private String productName;
    private String content;
    private List<String> colors;
    private List<String> sizes;
}
