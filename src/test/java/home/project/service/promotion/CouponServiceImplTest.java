package home.project.service.promotion;

import home.project.domain.elasticsearch.CouponDocument;
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
import home.project.dto.responseDTO.NotificationResponse;
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
import home.project.service.util.IndexToElasticsearch;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

        List<ProductCouponResponse> productCouponResponses = new ArrayList<>();
        List<MemberCouponResponse> memberCouponResponses = new ArrayList<>();

        couponResponse = new CouponResponse(
                1L,
                "TestCoupon",
                10,
                now,
                now.plusDays(30),
                "ALL",
                productCouponResponses,
                memberCouponResponses
        );

        // Security Context Mock 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    @DisplayName("쿠폰 생성 테스트")
    class CreateCouponTest {

        @Test
        @DisplayName("정상적으로 쿠폰을 생성한다")
        void createCouponSuccess() {
            // given
            when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);
            when(converter.convertFromCouponToCouponResponse(any(Coupon.class))).thenReturn(couponResponse);

            // when
            CouponResponse response = couponService.join(createCouponRequestDTO);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("TestCoupon");
            assertThat(response.getDiscountRate()).isEqualTo(10);
            assertThat(response.getStartDate()).isEqualTo(now);
            assertThat(response.getEndDate()).isEqualTo(now.plusDays(30));
            assertThat(response.getAssignBy()).isEqualTo("ALL");
            assertThat(response.getProductCouponResponse()).isEmpty();
            assertThat(response.getMemberCouponResponse()).isEmpty();
            verify(couponRepository).save(any(Coupon.class));
        }
    }

    @Nested
    @DisplayName("쿠폰 조회 테스트")
    class FindCouponTest {

        @Test
        @DisplayName("ID로 쿠폰을 조회한다")
        void findByIdSuccess() {
            // given
            when(couponRepository.findById(anyLong())).thenReturn(Optional.of(testCoupon));
            when(converter.convertFromCouponToCouponResponse(any(Coupon.class))).thenReturn(couponResponse);

            // when
            CouponResponse response = couponService.findByIdReturnCouponResponse(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("TestCoupon");
            assertThat(response.getDiscountRate()).isEqualTo(10);
            assertThat(response.getStartDate()).isEqualTo(now);
            assertThat(response.getEndDate()).isEqualTo(now.plusDays(30));
            verify(couponRepository).findById(anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 쿠폰 ID로 조회할 경우 실패한다")
        void findByIdFailNotFound() {
            // given
            when(couponRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> couponService.findById(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 쿠폰이 없습니다");
        }
    }

    @Nested
    @DisplayName("쿠폰 업데이트 테스트")
    class UpdateCouponTest {

        @Test
        @DisplayName("쿠폰 정보를 업데이트한다")
        void updateCouponSuccess() {
            // given
            CreateCouponRequestDTO updateRequest = new CreateCouponRequestDTO();
            updateRequest.setName("UpdatedCoupon");
            updateRequest.setDiscountRate(20);
            updateRequest.setStartDate(now.plusDays(1));
            updateRequest.setEndDate(now.plusDays(60));

            CouponResponse updatedResponse = new CouponResponse(
                    1L,
                    "UpdatedCoupon",
                    20,
                    now.plusDays(1),
                    now.plusDays(60),
                    "ALL",
                    new ArrayList<>(),
                    new ArrayList<>()
            );

            when(couponRepository.findById(anyLong())).thenReturn(Optional.of(testCoupon));
            when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);
            when(converter.convertFromCouponToCouponResponse(any(Coupon.class))).thenReturn(updatedResponse);

            // when
            CouponResponse response = couponService.updateCoupon(1L, updateRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("UpdatedCoupon");
            assertThat(response.getDiscountRate()).isEqualTo(20);
            verify(couponRepository).save(any(Coupon.class));
        }
    }

    @Nested
    @DisplayName("쿠폰 삭제 테스트")
    class DeleteCouponTest {

        @Test
        @DisplayName("쿠폰을 삭제한다")
        void deleteCouponSuccess() {
            // given
            when(couponRepository.findById(anyLong())).thenReturn(Optional.of(testCoupon));

            // when
            String deletedCouponName = couponService.deleteById(1L);

            // then
            assertThat(deletedCouponName).isEqualTo("TestCoupon");
            verify(couponRepository).deleteById(anyLong());
            verify(elasticsearchOperations).delete(anyString(), eq(CouponDocument.class));
        }
    }
}