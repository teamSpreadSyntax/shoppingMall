package home.project.service.order;

import home.project.domain.delivery.DeliveryStatusType;
import home.project.domain.delivery.DeliveryType;
import home.project.domain.delivery.Shipping;
import home.project.domain.elasticsearch.OrdersDocument;
import home.project.domain.member.Member;
import home.project.domain.member.MemberGradeType;
import home.project.domain.order.Orders;
import home.project.domain.product.*;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.requestDTO.ProductDTOForOrder;
import home.project.dto.responseDTO.OrderResponse;
import home.project.exceptions.exception.IdNotFoundException;
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
        now = LocalDateTime.now();

        // Member 설정
        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@example.com");
        testMember.setGrade(MemberGradeType.BRONZE);
        testMember.setAccumulatedPurchase(0L);
        testMember.setPoint(1000L);

        // Product 설정
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("TestProduct");
        testProduct.setPrice(10000L);
        testProduct.setStock(100L);

        // Shipping 설정
        testShipping = new Shipping();
        testShipping.setId(1L);
        testShipping.setDeliveryType(DeliveryType.STRAIGHT_DELIVERY);
        testShipping.setDeliveryAddress("Test Address");
        testShipping.setArrivingDate("2024-12-15");
        testShipping.setDeliveryStatus(DeliveryStatusType.ORDER_REQUESTED);
        testShipping.setDeliveryCost(3000L);
        testShipping.setShippingMessage("문 앞에 놓아주세요");

        // ProductDTOForOrder 설정
        ProductDTOForOrder productDTO = new ProductDTOForOrder();
        productDTO.setProductId(1L);
        productDTO.setQuantity(2);
        productDTO.setPrice(10000L);
        productDTOList = new ArrayList<>();
        productDTOList.add(productDTO);

        // ProductOrder 설정
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

        // OrderResponse 설정
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

        // CreateOrderRequestDTO 설정
        createOrderRequestDTO = new CreateOrderRequestDTO();
        createOrderRequestDTO.setProductOrders(productDTOList);
        createOrderRequestDTO.setCouponId(1L);
        createOrderRequestDTO.setPointsUsed(0L);

        // Security Context Mock 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        // Coupon 설정 수정
        testCoupon = new Coupon();
        testCoupon.setId(1L);
        testCoupon.setName("TestCoupon");
        testCoupon.setDiscountRate(10);
        testCoupon.setStartDate(now.minusDays(1));  // 시작일 설정
        testCoupon.setEndDate(now.plusDays(30));    // 종료일 설정

        // MemberCoupon 설정
        testMemberCoupon = new MemberCoupon();
        testMemberCoupon.setMember(testMember);
        testMemberCoupon.setCoupon(testCoupon);
        testMemberCoupon.setUsed(false);
    }

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
        assertThat(response.getDeliveryAddress()).isEqualTo("Test Address");
        assertThat(response.getTotalAmount()).isEqualTo(20000L);
        assertThat(response.getProducts()).hasSize(1);
        verify(orderRepository).save(any(Orders.class));
        verify(productService).decreaseStock(anyLong(), anyLong());
    }

    @Nested
    @DisplayName("주문 조회 테스트")
    class FindOrderTest {

        @Test
        @DisplayName("ID로 주문을 조회한다")
        void findByIdSuccess() {
            // given
            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));
            when(converter.convertFromOrderToOrderResponse(any(Orders.class))).thenReturn(orderResponse);

            // when
            OrderResponse response = orderService.findByIdReturnOrderResponse(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getOrderNum()).isEqualTo("TEST123");
            assertThat(response.getOrderDate()).isEqualTo(now);
            assertThat(response.getDeliveryAddress()).isEqualTo("Test Address");
            verify(orderRepository).findById(anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID로 조회할 경우 실패한다")
        void findByIdFailNotFound() {
            // given
            when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.findById(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 주문이 없습니다");
        }
    }

    @Nested
    @DisplayName("주문 구매확정 테스트")
    class ConfirmPurchaseTest {

        @Test
        @DisplayName("배송 완료된 주문을 구매 확정한다")
        void confirmPurchaseSuccess() {
            // given
            testProductOrder.setDeliveryStatus(DeliveryStatusType.DELIVERY_COMPLETED);
            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));

            // when
            orderService.confirmPurchase(1L);

            // then
            assertThat(testProductOrder.getDeliveryStatus()).isEqualTo(DeliveryStatusType.PURCHASE_CONFIRMED);
            verify(orderRepository).save(any(Orders.class));
        }
    }

    @Nested
    @DisplayName("주문 삭제 테스트")
    class DeleteOrderTest {

        @Test
        @DisplayName("주문을 삭제한다")
        void deleteOrderSuccess() {
            // given
            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));

            // when
            String deletedOrderNum = orderService.deleteById(1L);

            // then
            assertThat(deletedOrderNum).isEqualTo("TEST123");
            verify(orderRepository).deleteById(anyLong());
            verify(elasticsearchOperations).delete(anyString(), eq(OrdersDocument.class));
        }
    }
}