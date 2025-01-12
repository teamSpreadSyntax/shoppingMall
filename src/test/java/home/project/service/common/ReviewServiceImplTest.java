package home.project.service.common;

import home.project.domain.common.RatingType;
import home.project.domain.common.Review;
import home.project.domain.delivery.DeliveryStatusType;
import home.project.domain.delivery.Shipping;
import home.project.domain.member.Member;
import home.project.domain.order.Orders;
import home.project.domain.product.Product;
import home.project.dto.requestDTO.CreateReviewRequestDTO;
import home.project.dto.responseDTO.ReviewDetailResponse;
import home.project.dto.responseDTO.ReviewProductResponse;
import home.project.dto.responseDTO.ReviewResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.common.ReviewRepository;
import home.project.repository.order.OrderRepository;
import home.project.service.member.MemberService;
import home.project.service.product.ProductService;
import home.project.service.util.Converter;
import home.project.service.util.FileService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewServiceImplTest {

    @Mock
    private MemberService memberService;

    @Mock
    private ProductService productService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private Converter converter;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FileService fileService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Member testMember;
    private Product testProduct;
    private Orders testOrder;
    private Review testReview;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@test.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@test.com");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setBrand("Test Brand");
        testProduct.setMainImageFile("image_url");

        testOrder = new Orders();
        testOrder.setId(1L);
        testOrder.setOrderDate(LocalDateTime.now());

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
        Page<Orders> pagedOrders = new PageImpl<>(Collections.singletonList(testOrder));

        when(memberService.findByEmail(anyString())).thenReturn(testMember);
        when(orderRepository.findByMemberId(anyLong(), any(Pageable.class))).thenReturn(pagedOrders);

        Page<ReviewProductResponse> result = reviewService.getReviewableProducts(Pageable.unpaged());

        assertThat(result).isNotNull();
        verify(memberService).findByEmail(anyString());
        verify(orderRepository).findByMemberId(anyLong(), any(Pageable.class));
        }
    }
    @Test
    @DisplayName("리뷰 작성 가능한 상품 조회 - 회원이 존재하지 않는 경우")
    void getReviewableProductsMemberNotFound() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(memberService.findByEmail(anyString()))
                .thenThrow(new IdNotFoundException("회원이 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> reviewService.getReviewableProducts(pageable))
                .isInstanceOf(IdNotFoundException.class)
                .hasMessageContaining("회원이 존재하지 않습니다.");

        verify(memberService).findByEmail(anyString());
        verify(orderRepository, never()).findByMemberId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("리뷰 작성 가능한 상품 조회 - 주문이 없는 경우")
    void getReviewableProductsNoOrders() {
        // given
        Member member = new Member();
        member.setId(1L);
        Pageable pageable = PageRequest.of(0, 10);

        when(memberService.findByEmail(anyString())).thenReturn(member);
        when(orderRepository.findByMemberId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // when
        Page<ReviewProductResponse> result = reviewService.getReviewableProducts(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(memberService).findByEmail(anyString());
        verify(orderRepository).findByMemberId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("리뷰 작성 가능한 상품 조회 - 배송 정보가 없는 경우")
    void getReviewableProductsNoShippingInfo() {
        // given
        Member member = new Member();
        member.setId(1L);

        Orders order = new Orders();
        order.setId(1L);
        order.setShipping(null); // 배송 정보 없음

        Pageable pageable = PageRequest.of(0, 10);

        when(memberService.findByEmail(anyString())).thenReturn(member);
        when(orderRepository.findByMemberId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(order)));

        // when
        Page<ReviewProductResponse> result = reviewService.getReviewableProducts(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(memberService).findByEmail(anyString());
        verify(orderRepository).findByMemberId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("리뷰 작성 가능한 상품 조회 - 구매확정 상태가 아닌 경우")
    void getReviewableProductsNotPurchaseConfirmed() {
        // given
        Member member = new Member();
        member.setId(1L);

        Shipping shipping = new Shipping();
        shipping.setDeliveryStatus(DeliveryStatusType.IN_TRANSIT); // 배송중 상태

        Orders order = new Orders();
        order.setId(1L);
        order.setShipping(shipping);

        Pageable pageable = PageRequest.of(0, 10);

        when(memberService.findByEmail(anyString())).thenReturn(member);
        when(orderRepository.findByMemberId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(order)));

        // when
        Page<ReviewProductResponse> result = reviewService.getReviewableProducts(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(memberService).findByEmail(anyString());
        verify(orderRepository).findByMemberId(anyLong(), any(Pageable.class));
    }

    @Nested
    @DisplayName("리뷰 작성")
    class JoinReviewTest {
        @Test
        @DisplayName("리뷰 작성 성공")
        void joinReviewSuccess() {
            CreateReviewRequestDTO requestDTO = new CreateReviewRequestDTO();
            requestDTO.setRatingType(RatingType.FIVE);
            requestDTO.setDescription("Great product!");

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findByProductIdAndConfirmHasPurchase(anyLong())).thenReturn(testProduct);
            when(fileService.saveFile(any(), any(), any())).thenReturn("saved_image_url");
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
            when(converter.convertFromReviewToReviewDetailResponse(any(Review.class)))
                    .thenReturn(new ReviewDetailResponse(1L, "test@test.com", "Test Product", LocalDateTime.now(),
                            RatingType.FIVE, "Great product!", List.of("saved_image_url"), 0L));

            ReviewDetailResponse response = reviewService.join(1L, requestDTO, Collections.emptyList());

            assertThat(response).isNotNull();
            verify(reviewRepository).save(any(Review.class));
            verify(fileService, times(0)).saveFile(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("리뷰 유용성 증가")
    class IncreaseHelpfulTest {
        @Test
        @DisplayName("리뷰 유용성 카운트 증가 성공")
        void increaseHelpfulSuccess() {
            // given
            testReview.setHelpful(0L);
            ReviewDetailResponse reviewDetailResponse = new ReviewDetailResponse(
                    1L, "test@test.com", "Test Product", LocalDateTime.now(),
                    RatingType.FIVE, "Test Review", List.of(), 1L
            );

            when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
                Review review = invocation.getArgument(0);
                review.setHelpful(review.getHelpful() + 1);
                return review;
            });
            when(converter.convertFromReviewToReviewDetailResponse(any(Review.class))).thenReturn(reviewDetailResponse);

            // when
            ReviewDetailResponse response = reviewService.increaseHelpfulCount(1L);

            // then
            assertThat(response).isNotNull(); // null 확인
            assertThat(response.getHelpful()).isEqualTo(1L); // 증가 확인
            verify(reviewRepository).findById(anyLong()); // findById 호출 확인
            verify(reviewRepository).save(any(Review.class)); // save 호출 확인
        }
    }

    @Nested
    @DisplayName("리뷰 삭제")
    class DeleteReviewTest {
        @Test
        @DisplayName("리뷰 삭제 성공")
        void deleteByIdSuccess() {
            reviewService.deleteById(1L);
            verify(reviewRepository).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("리뷰 ID로 리뷰 조회")
    class FindReviewByIdTest {
        @Test
        @DisplayName("리뷰 ID로 리뷰 조회 성공")
        void findReviewByIdSuccess() {
            when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(testReview));
            when(converter.convertFromReviewToReviewDetailResponse(any(Review.class)))
                    .thenReturn(new ReviewDetailResponse(1L, "test@test.com", "Test Product", LocalDateTime.now(),
                            RatingType.FIVE, "Test Review", List.of(), 0L));

            ReviewDetailResponse response = reviewService.findReviewById(1L);

            assertThat(response).isNotNull();
            verify(reviewRepository).findById(anyLong());
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
            Page<ReviewResponse> pagedReviewResponses = new PageImpl<>(List.of(
                    new ReviewResponse(
                            1L,
                            "Test Product",
                            "test@test.com",
                            LocalDateTime.now()
                    )
            ));

            // Mock 데이터 설정
            when(memberService.findByEmail(anyString())).thenReturn(testMember); // 이메일 기반 회원 조회
            when(reviewRepository.findAllByMemberId(anyLong(), any(Pageable.class))).thenReturn(pagedReviews); // 회원의 리뷰 조회
            when(converter.convertFromPagedReviewToPagedReviewResponse(any(Page.class)))
                    .thenReturn(pagedReviewResponses); // 페이지 변환 처리

            // when
            Page<ReviewResponse> response = reviewService.findAllMyReview(Pageable.unpaged());

            // then
            assertThat(response).isNotNull(); // null 확인
            assertThat(response.getContent()).isNotEmpty(); // 결과 리스트가 비어있지 않음 확인
            assertThat(response.getContent().get(0).getReviewId()).isEqualTo(1L); // 리뷰 ID 확인
            assertThat(response.getContent().get(0).getProductName()).isEqualTo("Test Product"); // 상품 이름 확인
            assertThat(response.getContent().get(0).getMemberEmail()).isEqualTo("test@test.com"); // 회원 이메일 확인
            verify(memberService).findByEmail(anyString()); // 이메일 조회 호출 확인
            verify(reviewRepository).findAllByMemberId(anyLong(), any(Pageable.class)); // 리뷰 조회 호출 확인
            verify(converter).convertFromPagedReviewToPagedReviewResponse(any(Page.class)); // 변환 호출 확인
        }
    }
}
