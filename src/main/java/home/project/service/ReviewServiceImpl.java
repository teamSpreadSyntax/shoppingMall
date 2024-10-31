package home.project.service;

import home.project.domain.*;
import home.project.dto.requestDTO.CreateReviewRequestDTO;
import home.project.dto.responseDTO.*;
import home.project.repository.OrderRepository;
import home.project.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService{
    private final MemberService memberService;
    private final ProductService productService;
    private final OrderService orderService;
    private final ReviewRepository reviewRepository;
    private final Converter converter;
    private final OrderRepository orderRepository;


    @Override
    public Page<ReviewProductResponse> getReviewableProducts(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Page<Orders> pagedOrders = orderRepository.findByMemberId(member.getId(), pageable);

        // 각 주문의 배송 상태가 확인된 제품들을 ReviewProductResponse로 변환
        List<ReviewProductResponse> reviewProductResponses = pagedOrders.stream()
                .filter(order -> order.getShipping() != null && order.getShipping().getDeliveryStatus() == DeliveryStatusType.PURCHASE_CONFIRMED)
                .flatMap(order -> order.getProductOrders().stream()
                        .map(productOrder -> new ReviewProductResponse(
                                productOrder.getProduct().getId(),
                                productOrder.getProduct().getName(),
                                productOrder.getProduct().getBrand(),
                                order.getOrderDate(),
                                productOrder.getProduct().getImageUrl()
                        )))
                .collect(Collectors.toList());

        return new PageImpl<>(reviewProductResponses, pageable, reviewProductResponses.size());
    }

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
