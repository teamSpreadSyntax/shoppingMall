package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class ProductResponse {

    private Long id;
    private String name;
    private String brand;
    private String category;
    private String productNum;
    private Long price;
    private Integer discountRate;
    private List<String> description;
    private String mainImageFile;
    private boolean isLiked;
    private String size;
    private String color;

    private List<ProductCouponResponse> productCouponResponse;



    public ProductResponse(Long id, String name, String brand, String category, String productNum, Long price, Integer discountRate, List<String> description, String mainImageFile, boolean isLiked, String size, String color, List<ProductCouponResponse> productCouponResponse) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.productNum = productNum;
        this.price = price;
        this.discountRate = discountRate;
        this.description = description;
        this.mainImageFile = mainImageFile;
        this.isLiked = isLiked;
        this.size = size;
        this.color = color;
        this.productCouponResponse = productCouponResponse;
    }
}
