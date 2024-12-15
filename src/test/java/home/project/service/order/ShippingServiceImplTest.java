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

    @InjectMocks
    private ShippingServiceImpl shippingService;

    private Shipping testShipping;
    private Orders testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Orders();
        testOrder.setId(1L);
        testOrder.setOrderNum("ORDER12345");
        testOrder.setAmount(100000L);

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

    @Test
    @DisplayName("배송 상태를 업데이트 성공")
    void updateDeliveryStatusSuccess() {
        when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(testShipping));
        when(converter.convertFromShippingToShippingResponse(any())).thenAnswer(invocation -> {
            Shipping shipping = invocation.getArgument(0);
            return new ShippingResponse(
                    shipping.getId(),
                    shipping.getDeliveryNum(),
                    LocalDateTime.now(),
                    shipping.getDeliveryAddress(),
                    shipping.getOrders() != null ? shipping.getOrders().getAmount() : 0,
                    null,
                    shipping.getDeliveryType(),
                    shipping.getArrivedDate(),
                    shipping.getDepartureDate(),
                    shipping.getDeliveryStatus(),
                    shipping.getDeliveryCost(),
                    shipping.getOrders() != null && shipping.getOrders().getMember() != null
                            ? shipping.getOrders().getMember().getEmail()
                            : null
            );
        });

        // when
        ShippingResponse result = shippingService.update(1L, DeliveryStatusType.DELIVERY_STARTED);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDeliveryNum()).isEqualTo(testShipping.getDeliveryNum());
        assertThat(result.getDeliveryStatusType()).isEqualTo(DeliveryStatusType.DELIVERY_STARTED);
        verify(shippingRepository).save(any(Shipping.class));
    }

    @Test
    @DisplayName("배송 ID로 조회 성공")
    void findByIdSuccess() {
        // given
        when(shippingRepository.findById(anyLong())).thenReturn(Optional.of(testShipping));

        // when
        Shipping result = shippingService.findById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testShipping.getId());
        verify(shippingRepository).findById(anyLong());
    }

    @Test
    @DisplayName("배송 ID 조회 실패 - 존재하지 않는 ID")
    void findByIdFailNotFound() {
        // given
        when(shippingRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> shippingService.findById(99L))
                .isInstanceOf(IdNotFoundException.class)
                .hasMessageContaining("등록된 배송정보가 없습니다");
        verify(shippingRepository).findById(anyLong());
    }

    @Test
    @DisplayName("모든 배송 정보 조회 성공")
    void findAllSuccess() {
        // given
        Page<Shipping> pagedShipping = new PageImpl<>(List.of(testShipping));
        when(shippingRepository.findAll(any(Pageable.class))).thenReturn(pagedShipping);
        when(converter.convertFromPagedShippingToPagedShippingResponse(any(Page.class))).thenAnswer(invocation -> {
            Page<Shipping> shippingPage = invocation.getArgument(0);
            return new PageImpl<>(
                    shippingPage.map(shipping -> new ShippingResponse(
                            shipping.getId(),
                            shipping.getDeliveryNum(),
                            LocalDateTime.now(),
                            shipping.getDeliveryAddress(),
                            shipping.getOrders() != null ? shipping.getOrders().getAmount() : 0,
                            null,
                            shipping.getDeliveryType(),
                            shipping.getArrivedDate(),
                            shipping.getDepartureDate(),
                            shipping.getDeliveryStatus(),
                            shipping.getDeliveryCost(),
                            shipping.getOrders() != null && shipping.getOrders().getMember() != null
                                    ? shipping.getOrders().getMember().getEmail()
                                    : null
                    )).toList()
            );
        });

        // when
        Page<ShippingResponse> result = shippingService.findAll(PageRequest.of(0, 10));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDeliveryNum()).isEqualTo(testShipping.getDeliveryNum());
        verify(shippingRepository).findAll(any(Pageable.class));
    }
}
