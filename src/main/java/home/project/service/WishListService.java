package home.project.service;

import home.project.dto.requestDTO.AddWishRequestDTO;
import home.project.dto.responseDTO.WishListDetailResponse;
import home.project.dto.responseDTO.WishListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishListService {
    WishListResponse toggleWishList(Long productId);

    Page<WishListDetailResponse> findAllMyWishList(Pageable pageable);
}
