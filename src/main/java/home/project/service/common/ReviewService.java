package home.project.service.common;

import home.project.dto.requestDTO.CreateReviewRequestDTO;
import home.project.dto.responseDTO.ReviewDetailResponse;
import home.project.dto.responseDTO.ReviewProductResponse;
import home.project.dto.responseDTO.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {

    Page<ReviewProductResponse> getReviewableProducts(Pageable pageable);

    ReviewDetailResponse join(Long productOrderId, CreateReviewRequestDTO createReviewRequestDTO, List<MultipartFile> imageUrls);

    Page<ReviewResponse> findAllMyReview(Pageable pageable);

    Page<ReviewDetailResponse> findProductReview(Long productId, Pageable pageable);

    ReviewDetailResponse increaseHelpfulCount(Long reviewId);

    void deleteById(Long ReviewId);

    ReviewDetailResponse findReviewById(Long reviewId);
}
