package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ProductResponse {

    private Long id;
    private String name;
    private String brand;
    private String category;
    private String productNum;
    private Long price;
    private Integer discountRate;
    private String description;
    private String imageUrl;

    private List<String> sizes;
    private List<String> colors;

    private List<ProductCouponResponse> productCouponResponse;

    public ProductResponse(Long id, String name, String brand, String category, String productNum, Long price,
                           Integer discountRate, String description, String imageUrl,
                           List<String> sizes, List<String> colors, List<ProductCouponResponse> productCouponResponse) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.productNum = productNum;
        this.price = price;
        this.discountRate = discountRate;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sizes = sizes;
        this.colors = colors;
        this.productCouponResponse = productCouponResponse;
    }
}
