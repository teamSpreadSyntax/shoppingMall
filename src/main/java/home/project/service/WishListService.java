package home.project.service;

import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.WishListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishListService {

    WishListResponse toggleWishList(Long productId, boolean liked);

    Page<ProductResponse> findAllMyWishList(Pageable pageable);
}
