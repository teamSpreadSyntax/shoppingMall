package home.project.service.common;

import home.project.config.TestConfig;
import home.project.domain.common.RatingType;
import home.project.domain.common.Review;
import home.project.domain.member.Member;
import home.project.domain.order.Orders;
import home.project.domain.product.Product;
import home.project.dto.requestDTO.CreateReviewRequestDTO;
import home.project.dto.responseDTO.ReviewDetailResponse;
import home.project.dto.responseDTO.ReviewProductResponse;
import home.project.dto.responseDTO.ReviewResponse;
import home.project.repository.common.ReviewRepository;
import home.project.repository.order.OrderRepository;
import home.project.service.member.MemberService;
import home.project.service.product.ProductService;
import home.project.service.util.Converter;
import home.project.service.util.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(home.project.config.TestConfig.class) // 정확한 패키지 경로 사용
class ReviewServiceImplTest {

    @Mock
    private MemberService memberService;

    @Mock
    private ProductService productService;


    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Autowired
    private FileService fileService;

    @Mock
    private Converter converter;


    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Member testMember;
    private Product testProduct;
    private Orders testOrder;
    private Review testReview;

    @BeforeEach
    void setUp() {
        // SecurityContextHolder 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@test.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(fileService.saveFile(any(MultipartFile.class), anyString(), anyString()))
                .thenReturn("mock-file-path");

        // 테스트용 Member 객체 초기화
        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@test.com");

        // 테스트용 Product 객체 초기화
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setBrand("Test Brand");
        testProduct.setMainImageFile("image_url");

        // 테스트용 Orders 객체 초기화
        testOrder = new Orders();
        testOrder.setId(1L);
        testOrder.setOrderDate(LocalDateTime.now());

        // 테스트용 Review 객체 초기화
        testReview = new Review();
        testReview.setId(1L);
        testReview.setMember(testMember);
        testReview.setProduct(testProduct);
        testReview.setCreateAt(LocalDateTime.now());
        testReview.setDescription("Test Review");
        testReview.setHelpful(0L);
    }

    @Nested
    @DisplayName("리뷰 가능한 상품 조회")
    class GetReviewableProductsTest {

        @Test
        @DisplayName("리뷰 가능한 상품 조회 성공")
        void getReviewableProductsSuccess() {
            // given
            Page<Orders> pagedOrders = new PageImpl<>(Collections.singletonList(testOrder));

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(orderRepository.findByMemberId(anyLong(), any(Pageable.class))).thenReturn(pagedOrders);

            // when
            Page<ReviewProductResponse> result = reviewService.getReviewableProducts(Pageable.unpaged());

            // then
            assertThat(result).isNotNull();
            verify(memberService).findByEmail(anyString());
            verify(orderRepository).findByMemberId(anyLong(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("리뷰 작성")
    class JoinReviewTest {

        @Test
        @DisplayName("리뷰 작성 성공")
        void joinReviewSuccess() {
            // given
            CreateReviewRequestDTO requestDTO = new CreateReviewRequestDTO();
            requestDTO.setRatingType(RatingType.FIVE);
            requestDTO.setDescription("Great product!");

            List<MultipartFile> mockFiles = Collections.emptyList();
            List<String> savedImageUrls = List.of("saved_image_url");

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findByProductIdAndConfirmHasPurchase(anyLong())).thenReturn(testProduct);
            when(fileService.saveFile(any(MultipartFile.class), anyString(), anyString()))
                    .thenReturn("saved_image_url");
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
            when(converter.convertFromReviewToReviewDetailResponse(any(Review.class)))
                    .thenReturn(new ReviewDetailResponse(
                    1L,                       // reviewId
                    "test@test.com",          // memberEmail
                    "Test Product",           // productName
                    LocalDateTime.now(),      // createAt
                    RatingType.FIVE,          // ratingType
                    "Great product!",         // description
                    savedImageUrls,           // imageUrls
                    0L                        // helpful
            ));

            // when
            ReviewDetailResponse response = reviewService.join(1L, requestDTO, mockFiles);

            // then
            assertThat(response).isNotNull();
            verify(reviewRepository).save(any(Review.class));
            verify(fileService, times(mockFiles.size())).saveFile(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("리뷰 유용성 증가")
    class IncreaseHelpfulTest {

        @Test
        @DisplayName("리뷰 유용성 카운트 증가 성공")
        void increaseHelpfulSuccess() {
            // given
            when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
            when(converter.convertFromReviewToReviewDetailResponse(any(Review.class)))
                    .thenReturn(new ReviewDetailResponse(
                            1L,                       // reviewId
                            "test@test.com",          // memberEmail
                            "Test Product",           // productName
                            LocalDateTime.now(),      // createAt
                            RatingType.FIVE,          // ratingType
                            "Test Review",            // description
                            List.of("image1", "image2", "image3"), // imageUrls
                            1L                        // helpful
                    ));

            // when
            ReviewDetailResponse response = reviewService.increaseHelpfulCount(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(testReview.getHelpful()).isEqualTo(1L);
            verify(reviewRepository).findById(anyLong());
            verify(reviewRepository).save(any(Review.class));
        }
    }

    @Nested
    @DisplayName("리뷰 삭제")
    class DeleteReviewTest {

        @Test
        @DisplayName("리뷰 삭제 성공")
        void deleteByIdSuccess() {
            // when
            reviewService.deleteById(1L);

            // then
            verify(reviewRepository).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("내 리뷰 조회")
    class FindAllMyReviewTest {

        @Test
        @DisplayName("내 리뷰 조회 성공")
        void findAllMyReviewSuccess() {
            // given
            Page<Review> pagedReviews = new PageImpl<>(Collections.singletonList(testReview));
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(reviewRepository.findAllByMemberId(anyLong(), any(Pageable.class))).thenReturn(pagedReviews);
            when(converter.convertFromPagedReviewToPagedQnAResponse(any(Page.class)))
                    .thenReturn(Page.empty());

            // when
            Page<ReviewResponse> response = reviewService.findAllMyReview(Pageable.unpaged());

            // then
            assertThat(response).isNotNull();
            verify(reviewRepository).findAllByMemberId(anyLong(), any(Pageable.class));
        }
    }
}
