package home.project.service;

import home.project.domain.WishList;
import home.project.dto.requestDTO.AddWishRequestDTO;
import home.project.dto.responseDTO.WishListDetailResponse;
import home.project.dto.responseDTO.WishListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WishListService {

    WishListResponse toggleWishList(Long productId, boolean liked);

    Page<WishListDetailResponse> findAllMyWishList(Pageable pageable);

    List<WishList> findByMemberId(Long memberId);
}
