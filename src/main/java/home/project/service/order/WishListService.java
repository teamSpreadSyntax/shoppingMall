package home.project.service.order;

import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.WishListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface WishListService {


    @Transactional
    WishListResponse addToWishList(Long productId);

    @Transactional
    WishListResponse removeFromWishList(Long productId);

    Page<ProductResponse> findAllMyWishList(Pageable pageable);
}
