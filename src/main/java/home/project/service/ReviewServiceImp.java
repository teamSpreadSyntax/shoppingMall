package home.project.service;

import home.project.domain.*;
import home.project.dto.requestDTO.CreateReviewRequestDTO;
import home.project.dto.responseDTO.ReviewDetailResponse;
import home.project.dto.responseDTO.ReviewResponse;
import home.project.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewServiceImp implements ReviewService{
    private final MemberService memberService;
    private final ProductService productService;
    private final ReviewRepository reviewRepository;
    private final Converter converter;

    @Override
    @Transactional
    public ReviewDetailResponse join(Long productOrderId, CreateReviewRequestDTO createReviewRequestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Product product = productService.findByProductOrderNum(productOrderId);

        Long helpful = 0L;
        Review review = new Review();
        review.setMember(member);
        review.setProduct(product);
        review.setCreateAt(LocalDateTime.now());
        review.setRating(createReviewRequestDTO.getRating());
        review.setDescription(createReviewRequestDTO.getDescription());
        review.setHelpful(helpful);

        review.setImageUrl1(createReviewRequestDTO.getImageUrl1());
        review.setImageUrl2(createReviewRequestDTO.getImageUrl2());
        review.setImageUrl3(createReviewRequestDTO.getImageUrl3());

        reviewRepository.save(review);


        return converter.convertFromReviewToReviewDetailResponse(review);
    }

    @Override
    public Page<ReviewResponse> findAllMyReview(Pageable pageable){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Page<Review> pagedReview = reviewRepository.findAllByMemberId(member.getId(), pageable);

        return converter.convertFromPagedReviewToPagedQnAResponse(pagedReview);
    }

    @Override
    @Transactional
    public ReviewDetailResponse increaseHelpfulCount(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        review.setHelpful(review.getHelpful() + 1);

        reviewRepository.save(review);

        return converter.convertFromReviewToReviewDetailResponse(review);
    }

    @Override
    @Transactional
    public void deleteById(Long ReviewId) {
        reviewRepository.deleteById(ReviewId);
    }
}
