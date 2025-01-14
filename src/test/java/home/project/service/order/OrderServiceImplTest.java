package home.project.service.order;

import home.project.domain.delivery.DeliveryStatusType;
import home.project.domain.delivery.DeliveryType;
import home.project.domain.delivery.Shipping;
import home.project.domain.member.Member;
import home.project.domain.member.MemberGradeType;
import home.project.domain.order.Orders;
import home.project.domain.product.*;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.requestDTO.ProductDTOForOrder;
import home.project.dto.responseDTO.OrderResponse;
import home.project.exceptions.exception.InvalidCouponException;
import home.project.repository.member.MemberRepository;
import home.project.repository.order.OrderRepository;
import home.project.repository.order.ProductOrderRepository;
import home.project.repository.promotion.MemberCouponRepository;
import home.project.repositoryForElasticsearch.OrdersElasticsearchRepository;
import home.project.service.member.MemberService;
import home.project.service.product.ProductService;
import home.project.service.promotion.CouponService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private ProductService productService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberCouponRepository memberCouponRepository;
    @Mock
    private CouponService couponService;
    @Mock
    private Converter converter;
    @Mock
    private IndexToElasticsearch indexToElasticsearch;
    @Mock
    private OrdersElasticsearchRepository ordersElasticsearchRepository;
    @Mock
    private ElasticsearchOperations elasticsearchOperations;
    @Mock
    private ProductOrderRepository productOrderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Member testMember;
    private Product testProduct;
    private Orders testOrder;
    private Coupon testCoupon;
    private MemberCoupon testMemberCoupon;
    private CreateOrderRequestDTO createOrderRequestDTO;
    private OrderResponse orderResponse;
    private Shipping testShipping;
    private ProductOrder testProductOrder;
    private LocalDateTime now;
    private List<ProductDTOForOrder> productDTOList;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        now = LocalDateTime.now();

        // Member 설정
        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@example.com");
        testMember.setGrade(MemberGradeType.BRONZE);
        testMember.setAccumulatedPurchase(0L);
        testMember.setPoint(1000L);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("TestProduct");
        testProduct.setPrice(10000L);
        testProduct.setStock(100L);

        testShipping = new Shipping();
        testShipping.setId(1L);
        testShipping.setDeliveryType(DeliveryType.STRAIGHT_DELIVERY);
        testShipping.setDeliveryAddress("Test Address");
        testShipping.setArrivingDate("2024-12-15");
        testShipping.setDeliveryStatus(DeliveryStatusType.ORDER_REQUESTED);
        testShipping.setDeliveryCost(3000L);
        testShipping.setShippingMessage("문 앞에 놓아주세요");

        ProductDTOForOrder productDTO = new ProductDTOForOrder();
        productDTO.setProductId(1L);
        productDTO.setQuantity(2);
        productDTO.setPrice(10000L);
        productDTOList = new ArrayList<>();
        productDTOList.add(productDTO);

        testProductOrder = new ProductOrder();
        testProductOrder.setProduct(testProduct);
        testProductOrder.setQuantity(2);
        testProductOrder.setPrice(10000L);
        testProductOrder.setDeliveryStatus(DeliveryStatusType.ORDER_REQUESTED);

        // Order 설정
        testOrder = new Orders();
        testOrder.setId(1L);
        testOrder.setOrderNum("TEST123");
        testOrder.setOrderDate(now);
        testOrder.setMember(testMember);
        testOrder.setAmount(20000L);
        testOrder.setShipping(testShipping);
        testOrder.setPointsUsed(0L);
        testOrder.setPointsEarned(1000L);
        testOrder.getProductOrders().add(testProductOrder);
        testShipping.setOrders(testOrder);

        orderResponse = new OrderResponse(
                1L,
                "TEST123",
                now,
                "Test Address",
                20000L,
                0L,
                1000L,
                productDTOList
        );

        createOrderRequestDTO = new CreateOrderRequestDTO();
        createOrderRequestDTO.setProductOrders(productDTOList);
        createOrderRequestDTO.setCouponId(1L);
        createOrderRequestDTO.setPointsUsed(0L);

        testCoupon = new Coupon();
        testCoupon.setId(1L);
        testCoupon.setName("TestCoupon");
        testCoupon.setDiscountRate(10);
        testCoupon.setStartDate(now.minusDays(1));
        testCoupon.setEndDate(now.plusDays(30));

        testMemberCoupon = new MemberCoupon();
        testMemberCoupon.setMember(testMember);
        testMemberCoupon.setCoupon(testCoupon);
        testMemberCoupon.setUsed(false);
    }

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateOrderTest {
        @Test
        @DisplayName("정상적으로 주문을 생성한다")
        void createOrderSuccess() {
            // given
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(couponService.findById(anyLong())).thenReturn(testCoupon);
            when(memberCouponRepository.findByMemberAndCoupon(any(Member.class), any(Coupon.class)))
                    .thenReturn(Optional.of(testMemberCoupon));
            when(orderRepository.save(any(Orders.class))).thenReturn(testOrder);
            when(converter.convertFromOrderToOrderResponse(any(Orders.class))).thenReturn(orderResponse);
            when(converter.convertFromCreateOrderRequestDTOToShipping(any(CreateOrderRequestDTO.class)))
                    .thenReturn(testShipping);

            // when
            OrderResponse response = orderService.join(createOrderRequestDTO);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getOrderNum()).isEqualTo("TEST123");
            verify(orderRepository).save(any(Orders.class));
            verify(productService).decreaseStock(anyLong(), anyLong());
        }

        @Test
        @DisplayName("재고가 부족한 경우 주문 생성에 실패한다")
        void createOrderFailInsufficientStock() {
            // given
            testProduct.setStock(1L);
            createOrderRequestDTO.setCouponId(null);

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(converter.convertFromCreateOrderRequestDTOToShipping(any(CreateOrderRequestDTO.class)))
                    .thenReturn(testShipping);
            when(productService.decreaseStock(anyLong(), anyLong()))
                    .thenThrow(new IllegalStateException("재고가 부족합니다"));

            // when & then
            assertThatThrownBy(() -> orderService.join(createOrderRequestDTO))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("재고가 부족합니다");
        }

        @Test
        @DisplayName("유효하지 않은 쿠폰으로 주문 시 실패한다")
        void createOrderFailInvalidCoupon() {
            // given
            testCoupon.setEndDate(now.minusDays(1));
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(couponService.findById(anyLong())).thenReturn(testCoupon);
            when(converter.convertFromCreateOrderRequestDTOToShipping(any(CreateOrderRequestDTO.class)))
                    .thenReturn(testShipping);

            // when & then
            assertThatThrownBy(() -> orderService.join(createOrderRequestDTO))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessageContaining("쿠폰이 유효하지 않습니다");
        }
        @Test
        @DisplayName("포인트가 부족한 경우 주문 생성에 실패한다")
        void createOrderFailInsufficientPoints() {
            // given
            testMember.setPoint(0L);
            createOrderRequestDTO.setPointsUsed(1000L);
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(converter.convertFromCreateOrderRequestDTOToShipping(any(CreateOrderRequestDTO.class)))
                    .thenReturn(testShipping);

            // when & then
            assertThatThrownBy(() -> orderService.join(createOrderRequestDTO))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("포인트가 부족합니다");
        }
    }

    @Nested
    @DisplayName("회원 등급 변경 테스트")
    class MemberGradeTest {
        @Test
        @DisplayName("누적 구매액에 따라 회원 등급이 올라간다")
        void memberGradeUpgradeSuccess() {
            // given
            testMember.setAccumulatedPurchase(180000L);
            testOrder.setAmount(30000L);
            createOrderRequestDTO.setCouponId(null);

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(orderRepository.save(any(Orders.class))).thenReturn(testOrder);
            when(converter.convertFromOrderToOrderResponse(any(Orders.class))).thenReturn(orderResponse);
            when(converter.convertFromCreateOrderRequestDTOToShipping(any(CreateOrderRequestDTO.class)))
                    .thenReturn(testShipping);

            // when
            orderService.join(createOrderRequestDTO);

            // then
            assertThat(testMember.getGrade()).isEqualTo(MemberGradeType.GOLD);
            verify(memberRepository).save(testMember);
        }
    }

    @Nested
    @DisplayName("주문 상태 변경 테스트")
    class OrderStatusTest {
        @Test
        @DisplayName("배송 완료 전 구매 확정 시도시 실패한다")
        void confirmPurchaseFailBeforeDelivery() {
            // given
            testProductOrder.setDeliveryStatus(DeliveryStatusType.DELIVERY_STARTED);
            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));

            // when & then
            assertThatThrownBy(() -> orderService.confirmPurchase(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("배송이 완료된 상품만 구매 확정이 가능합니다");
        }

        @Test
        @DisplayName("주문 취소시 재고가 정상적으로 복구된다")
        void deleteOrderRestoreStockSuccess() {
            // given
            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));
            Long initialStock = testProduct.getStock();

            // when
            orderService.deleteById(1L);

            // then
            verify(productService).increaseStock(anyLong(), eq(2L));
            verify(productService).decreaseSoldQuantity(anyLong(), eq(2L));
        }
    }

    @Nested
    @DisplayName("주문 조회 테스트")
    class OrderSearchTest {
        @Test
        @DisplayName("회원 ID로 주문 목록을 조회한다")
        void findByMemberIdSuccess() {
            // given
            List<Orders> ordersList = List.of(testOrder);
            Page<Orders> ordersPage = new PageImpl<>(ordersList);
            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(testMember));
            when(orderRepository.findByMemberId(anyLong(), any(Pageable.class))).thenReturn(ordersPage);
            when(converter.convertFromPagedOrderToPagedOrderResponse(any())).thenReturn(new PageImpl<>(List.of(orderResponse)));

            // when
            Page<OrderResponse> response = orderService.findByMemberId(Pageable.unpaged());

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            verify(orderRepository).findByMemberId(eq(testMember.getId()), any(Pageable.class));
        }
    }
}