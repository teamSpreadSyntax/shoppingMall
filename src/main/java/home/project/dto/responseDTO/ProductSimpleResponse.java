package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductSimpleResponse {

    private Long id;
    private String name;
    private String brand;
    private Long price;
    private Integer discountRate;
    private String imageUrl;
    private boolean isLiked;
    private String color;


    public ProductSimpleResponse(Long id, String name, String brand, Long price, Integer discountRate, String imageUrl, boolean isLiked, String color) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.discountRate = discountRate;
        this.imageUrl = imageUrl;
        this.isLiked = isLiked;
        this.color = color;
    }
}
