package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class ProductWithQnAAndReviewResponse {

    private Long id;
    private String name;
    private String brand;
    private String category;
    private String productNum;
    private Long price;
    private Integer discountRate;
    private String description;
    private String imageUrl;
    private boolean isLiked;

    private String size;
    private String color;

    private List<ProductCouponResponse> productCouponResponses;

    private Page<QnADetailResponse> qnADetailResponses;

    private Page<ReviewDetailResponse> reviewDetailResponses;


    public ProductWithQnAAndReviewResponse(Long id, String name, String brand, String category, String productNum, Long price, Integer discountRate, String description, String imageUrl, boolean isLiked, String size, String color, List<ProductCouponResponse> productCouponResponses, Page<QnADetailResponse> qnADetailResponses, Page<ReviewDetailResponse> reviewDetailResponses) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.productNum = productNum;
        this.price = price;
        this.discountRate = discountRate;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isLiked = isLiked;
        this.size = size;
        this.color = color;
        this.productCouponResponses = productCouponResponses;
        this.qnADetailResponses = qnADetailResponses;
        this.reviewDetailResponses = reviewDetailResponses;
    }
}
