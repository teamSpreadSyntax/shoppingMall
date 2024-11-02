package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSellerResponse {

    private Long id;
    private String name;
    private String brand;
    private Long price;
    private String description;
    private String imageUrl;

    private Long sellerId;
    private String sellerName;
    private String sellerContactInfo;

    public ProductSellerResponse(Long id, String name, String brand, Long price, String description, String imageUrl,
                                 Long sellerId, String sellerName, String sellerContactInfo, String email, String address) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.sellerContactInfo = sellerContactInfo;
    }
}
