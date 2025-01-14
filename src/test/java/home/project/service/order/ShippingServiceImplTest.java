package home.project.service.order;

import home.project.domain.delivery.DeliveryStatusType;
import home.project.domain.delivery.DeliveryType;
import home.project.domain.delivery.Shipping;
import home.project.domain.order.Orders;
import home.project.dto.responseDTO.ShippingResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.order.OrderRepository;
import home.project.repository.order.ProductOrderRepository;
import home.project.repository.shipping.ShippingRepository;
import home.project.service.order.OrderService;
import home.project.service.order.ShippingServiceImpl;
import home.project.service.product.ProductService;
import home.project.service.util.Converter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import home.project.domain.delivery.DeliveryStatusType;
import home.project.domain.delivery.DeliveryType;
import home.project.domain.delivery.Shipping;
import home.project.domain.member.Member;
import home.project.domain.member.MemberGradeType;
import home.project.domain.order.Orders;
import home.project.domain.product.Product;
import home.project.domain.product.ProductOrder;
import home.project.dto.responseDTO.ShippingResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.member.MemberRepository;
import home.project.repository.order.OrderRepository;
import home.project.repository.order.ProductOrderRepository;
import home.project.repository.shipping.ShippingRepository;
import home.project.service.product.ProductService;
import home.project.service.util.Converter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingServiceImplTest {

    @Mock
    private ShippingRepository shippingRepository;
    @Mock
    private ProductOrderRepository productOrderRepository;
    @Mock
    private Converter converter;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private OrderService orderService;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ShippingServiceImpl shippingService;

    private Shipping testShipping;
    private Orders testOrder;
    private Member testMember;
    private Product testProduct;
    private ProductOrder testProductOrder;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@test.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Member 설정
        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@example.com");
        testMember.setPoint(2000L);
        testMember.setAccumulatedPurchase(200000L);
        testMember.setGrade(MemberGradeType.GOLD);

        // Product 설정
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setStock(100L);

        // ProductOrder 설정
        testProductOrder = new ProductOrder();
        testProductOrder.setProduct(testProduct);
        testProductOrder.setQuantity(2);
        testProductOrder.setDeliveryStatus(DeliveryStatusType.ORDER_REQUESTED);

        // Order 설정
        testOrder = new Orders();
        testOrder.setId(1L);
        testOrder.setOrderNum("ORDER12345");
        testOrder.setAmount(100000L);
        testOrder.setPointsUsed(1000L);
        testOrder.setPointsEarned(500L);
        testOrder.setMember(testMember);
        testOrder.getProductOrders().add(testProductOrder);

        // Shipping 설정
        testShipping = new Shipping();
        testShipping.setId(1L);
        testShipping.setDeliveryNum("SHIP202312010000");
        testShipping.setDeliveryType(DeliveryType.ORDINARY_DELIVERY);
        testShipping.setDeliveryAddress("서울시 강남구");
        testShipping.setArrivingDate("2024-12-15");
        testShipping.setDeliveryCost(5000L);
        testShipping.setDeliveryStatus(DeliveryStatusType.ORDER_REQUESTED);
        testShipping.setOrders(testOrder);
    }

    @Nested
    @DisplayName("배송 상태 업데이트 테스트")
    class UpdateDeliveryStatusTest {
        @Test
        @DisplayName("배송 시작으로 상태 변경 시 배송번호가 생성된다")
        void updateToDeliveryStartedSuccess() {
            when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(testShipping));
            when(shippingRepository.save(any(Shipping.class))).thenReturn(testShipping);
            when(converter.convertFromShippingToShippingResponse(any())).thenReturn(
                    new ShippingResponse(1L, "SHIP202312010000", LocalDateTime.now(),
                            "서울시 강남구", 100000L, null, DeliveryType.ORDINARY_DELIVERY,
                            "2024-12-15", LocalDateTime.now().toString(),
                            DeliveryStatusType.DELIVERY_STARTED, 5000L, "test@example.com")
            );

            ShippingResponse result = shippingService.update(1L, DeliveryStatusType.DELIVERY_STARTED);

            assertThat(result).isNotNull();
            assertThat(result.getDeliveryStatusType()).isEqualTo(DeliveryStatusType.DELIVERY_STARTED);
            assertThat(result.getDepartureDate()).isNotNull();
            assertThat(result.getDeliveryNum()).startsWith("SHIP");
            verify(shippingRepository).save(any(Shipping.class));
            verify(productOrderRepository).saveAll(any());
        }

        @Test
        @DisplayName("주문 취소 완료로 상태 변경 시 재고와 포인트가 복구된다")
        void updateToOrderCancellationSuccess() {
            // given
            when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(testShipping));
            when(shippingRepository.save(any(Shipping.class))).thenReturn(testShipping);
            when(converter.convertFromShippingToShippingResponse(any())).thenReturn(
                    new ShippingResponse(1L, "SHIP202312010000", LocalDateTime.now(),
                            "서울시 강남구", 100000L, null, DeliveryType.ORDINARY_DELIVERY,
                            "2024-12-15", null, DeliveryStatusType.ORDER_CANCELLATION_COMPLETED,
                            5000L, "test@example.com")
            );

            // when
            ShippingResponse result = shippingService.update(1L, DeliveryStatusType.ORDER_CANCELLATION_COMPLETED);

            // then
            assertThat(testMember.getPoint()).isEqualTo(2500L);
            assertThat(testMember.getAccumulatedPurchase()).isEqualTo(100000L);
            assertThat(testMember.getGrade()).isEqualTo(MemberGradeType.SILVER);
            verify(productService).increaseStock(eq(1L), eq(2L));
            verify(productService).decreaseSoldQuantity(eq(1L), eq(2L));
            verify(memberRepository).save(testMember);
            verify(productOrderRepository).saveAll(any());
        }

        @Test
        @DisplayName("환불 완료로 상태 변경 시 회원 등급이 조정된다")
        void updateToRefundCompletedSuccess() {
            // given
            when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(testShipping));
            when(shippingRepository.save(any(Shipping.class))).thenReturn(testShipping);
            when(converter.convertFromShippingToShippingResponse(any())).thenReturn(
                    new ShippingResponse(1L, "SHIP202312010000", LocalDateTime.now(),
                            "서울시 강남구", 100000L, null, DeliveryType.ORDINARY_DELIVERY,
                            "2024-12-15", null, DeliveryStatusType.REFUND_COMPLETED,
                            5000L, "test@example.com")
            );

            ShippingResponse result = shippingService.update(1L, DeliveryStatusType.REFUND_COMPLETED);


            assertThat(testMember.getAccumulatedPurchase()).isEqualTo(100000L);
            assertThat(testMember.getGrade()).isEqualTo(MemberGradeType.SILVER);

            assertThat(result).isNotNull();
            assertThat(result.getDeliveryNum()).isEqualTo("SHIP202312010000");
            assertThat(result.getDeliveryStatusType()).isEqualTo(DeliveryStatusType.REFUND_COMPLETED);

            verify(memberRepository).save(testMember);
            verify(productOrderRepository).saveAll(any());
        }
    }

    @Nested
    @DisplayName("배송 조회 테스트")
    class ShippingQueryTest {
        @Test
        @DisplayName("배송 ID로 조회 성공")
        void findByIdSuccess() {
            when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(testShipping));

            Shipping result = shippingService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testShipping.getId());
            verify(shippingRepository).findById(anyLong());
        }

        @Test
        @DisplayName("배송 ID 조회 실패 - 존재하지 않는 ID")
        void findByIdFailNotFound() {
            when(shippingRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> shippingService.findById(99L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 배송정보가 없습니다");
            verify(shippingRepository).findById(anyLong());
        }

        @Test
        @DisplayName("배송 ID로 ShippingResponse 조회 성공")
        void findByIdReturnShippingResponseSuccess() {
            // given
            when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(testShipping));
            when(converter.convertFromShippingToShippingResponse(any())).thenReturn(
                    new ShippingResponse(1L, "SHIP202312010000", LocalDateTime.now(),
                            "서울시 강남구", 100000L, null, DeliveryType.ORDINARY_DELIVERY,
                            "2024-12-15", null, DeliveryStatusType.ORDER_REQUESTED,
                            5000L, "test@example.com")
            );

            ShippingResponse result = shippingService.findByIdReturnShippingResponse(1L);

            assertThat(result).isNotNull();
            assertThat(result.getDeliveryNum()).isEqualTo(testShipping.getDeliveryNum());
            verify(converter).convertFromShippingToShippingResponse(any());
        }

        @Test
        @DisplayName("모든 배송 정보 조회 성공")
        void findAllSuccess() {
            // given
            Page<Shipping> pagedShipping = new PageImpl<>(List.of(testShipping));
            when(shippingRepository.findAll(any(Pageable.class))).thenReturn(pagedShipping);
            when(converter.convertFromPagedShippingToPagedShippingResponse(any())).thenReturn(
                    new PageImpl<>(List.of(
                            new ShippingResponse(1L, "SHIP202312010000", LocalDateTime.now(),
                                    "서울시 강남구", 100000L, null, DeliveryType.ORDINARY_DELIVERY,
                                    "2024-12-15", null, DeliveryStatusType.ORDER_REQUESTED,
                                    5000L, "test@example.com")
                    ))
            );

            // when
            Page<ShippingResponse> result = shippingService.findAll(PageRequest.of(0, 10));

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getDeliveryNum()).isEqualTo(testShipping.getDeliveryNum());
            verify(shippingRepository).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("검색 조건으로 배송 목록을 조회한다")
        void findShippingsSuccess() {
            Page<Shipping> pagedShipping = new PageImpl<>(List.of(testShipping));
            when(shippingRepository.findShippings(anyString(), anyString(), anyString(),
                    anyString(), anyString(), any(Pageable.class))).thenReturn(pagedShipping);
            when(converter.convertFromPagedShippingToPagedShippingResponse(any())).thenReturn(
                    new PageImpl<>(List.of(
                            new ShippingResponse(1L, "SHIP202312010000", LocalDateTime.now(),
                                    "서울시 강남구", 100000L, null, DeliveryType.ORDINARY_DELIVERY,
                                    "2024-12-15", null, DeliveryStatusType.ORDER_REQUESTED,
                                    5000L, "test@example.com")
                    ))
            );

            Page<ShippingResponse> result = shippingService.findShippings(
                    "SHIP202312010000", "20231201", "PROD123",
                    "test@example.com", "배송", PageRequest.of(0, 10)
            );

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getDeliveryNum()).isEqualTo("SHIP202312010000");
            verify(shippingRepository).findShippings(anyString(), anyString(), anyString(),
                    anyString(), anyString(), any(Pageable.class));
        }
    }
}
