package home.project.service.promotion;

import home.project.domain.member.Member;
import home.project.domain.product.Coupon;
import home.project.domain.product.Product;
import home.project.domain.product.AssignType;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.requestDTO.AssignCouponToMemberRequestDTO;
import home.project.dto.requestDTO.AssignCouponToProductRequestDTO;
import home.project.dto.responseDTO.CouponResponse;
import home.project.dto.responseDTO.MemberCouponResponse;
import home.project.dto.responseDTO.ProductCouponResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.member.MemberRepository;
import home.project.repository.product.ProductRepository;
import home.project.repository.promotion.CouponRepository;
import home.project.repository.promotion.MemberCouponRepository;
import home.project.repository.promotion.ProductCouponRepository;
import home.project.service.member.MemberService;
import home.project.service.notification.NotificationService;
import home.project.service.notification.WebSocketNotificationService;
import home.project.service.util.Converter;
import home.project.service.integration.IndexToElasticsearch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CouponServiceImplTest {

    @Mock
    private CouponRepository couponRepository;
    @Mock
    private MemberCouponRepository memberCouponRepository;
    @Mock
    private ProductCouponRepository productCouponRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private Converter converter;
    @Mock
    private MemberService memberService;
    @Mock
    private IndexToElasticsearch indexToElasticsearch;
    @Mock
    private ElasticsearchOperations elasticsearchOperations;
    @Mock
    private WebSocketNotificationService webSocketNotificationService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Coupon testCoupon;
    private Member testMember;
    private Product testProduct;
    private CreateCouponRequestDTO createCouponRequestDTO;
    private CouponResponse couponResponse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        testCoupon = new Coupon();
        testCoupon.setId(1L);
        testCoupon.setName("TestCoupon");
        testCoupon.setDiscountRate(10);
        testCoupon.setStartDate(now);
        testCoupon.setEndDate(now.plusDays(30));
        testCoupon.setAssignBy("ALL");

        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@example.com");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setProductNum("TEST001");

        createCouponRequestDTO = new CreateCouponRequestDTO();
        createCouponRequestDTO.setName("TestCoupon");
        createCouponRequestDTO.setDiscountRate(10);
        createCouponRequestDTO.setStartDate(now);
        createCouponRequestDTO.setEndDate(now.plusDays(30));

        couponResponse = new CouponResponse(
                1L,
                "TestCoupon",
                10,
                now,
                now.plusDays(30),
                "ALL",
                List.of(),
                List.of()
        );
    }

    @Nested
    @DisplayName("쿠폰 생성 테스트")
    class CreateCouponTest {

        @Test
        @DisplayName("쿠폰 생성 성공")
        void createCouponSuccess() {
            when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);
            when(converter.convertFromCouponToCouponResponse(any(Coupon.class))).thenReturn(couponResponse);

            CouponResponse response = couponService.join(createCouponRequestDTO);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("TestCoupon");
            verify(couponRepository).save(any(Coupon.class));
        }

        @Test
        @DisplayName("쿠폰 생성 실패 - 이름 누락")
        void createCouponFailNoName() {
            // given
            createCouponRequestDTO.setName(null);

            // when & then
            assertThatThrownBy(() -> couponService.join(createCouponRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰 이름은 필수입니다.");
        }
    }

    @Nested
    @DisplayName("쿠폰 수정 테스트")
    class UpdateCouponTest {

        @Test
        @DisplayName("쿠폰 수정 성공")
        void updateCouponSuccess() {
            when(couponRepository.findById(anyLong())).thenReturn(Optional.of(testCoupon));
            when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);
            when(converter.convertFromCouponToCouponResponse(any(Coupon.class))).thenReturn(couponResponse);

            CouponResponse response = couponService.updateCoupon(1L, createCouponRequestDTO);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("TestCoupon");
            verify(couponRepository).save(any(Coupon.class));
        }
    }

    @Nested
    @DisplayName("쿠폰 삭제 테스트")
    class DeleteCouponTest {

        @Test
        @DisplayName("쿠폰 삭제 성공")
        void deleteCouponSuccess() {
            when(couponRepository.findById(anyLong())).thenReturn(Optional.of(testCoupon));

            String deletedCouponName = couponService.deleteById(1L);

            assertThat(deletedCouponName).isEqualTo("TestCoupon");
            verify(couponRepository).deleteById(anyLong());
        }

        @Test
        @DisplayName("쿠폰 삭제 실패 - 쿠폰 없음")
        void deleteCouponFailNotFound() {
            when(couponRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.deleteById(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 쿠폰이 없습니다.");
        }
    }

    @Nested
    @DisplayName("쿠폰 할당 테스트")
    class AssignCouponTest {

        @Test
        @DisplayName("회원에게 쿠폰 할당 성공")
        void assignCouponToMemberSuccess() {
            AssignCouponToMemberRequestDTO requestDTO = new AssignCouponToMemberRequestDTO();
            requestDTO.setCouponId(1L);
            requestDTO.setAssignType(AssignType.ALL);

            when(couponRepository.findById(anyLong())).thenReturn(Optional.of(testCoupon));
            when(memberRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

            Page<MemberCouponResponse> response = couponService.assignCouponToMember(requestDTO, Pageable.unpaged());

            assertThat(response).isNotNull();
            verify(couponRepository).findById(anyLong());
        }

        @Test
        @DisplayName("회원에게 쿠폰 할당 실패 - 쿠폰 없음")
        void assignCouponToMemberFailCouponNotFound() {
            AssignCouponToMemberRequestDTO requestDTO = new AssignCouponToMemberRequestDTO();
            requestDTO.setCouponId(1L);

            when(couponRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.assignCouponToMember(requestDTO, Pageable.unpaged()))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 쿠폰이 없습니다.");
        }

        @Test
        @DisplayName("상품에 쿠폰 할당 성공")
        void assignCouponToProductSuccess() {
            AssignCouponToProductRequestDTO requestDTO = new AssignCouponToProductRequestDTO();
            requestDTO.setCouponId(1L);
            requestDTO.setAssignType(AssignType.ALL);

            when(couponRepository.findById(anyLong())).thenReturn(Optional.of(testCoupon));
            when(productRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

            Page<ProductCouponResponse> response = couponService.assignCouponToProduct(requestDTO, Pageable.unpaged());

            assertThat(response).isNotNull();
            verify(couponRepository).findById(anyLong());
        }

        @Test
        @DisplayName("상품에 쿠폰 할당 실패 - 쿠폰 없음")
        void assignCouponToProductFailCouponNotFound() {
            AssignCouponToProductRequestDTO requestDTO = new AssignCouponToProductRequestDTO();
            requestDTO.setCouponId(1L);

            when(couponRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.assignCouponToProduct(requestDTO, Pageable.unpaged()))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 쿠폰이 없습니다.");
        }
    }
}
