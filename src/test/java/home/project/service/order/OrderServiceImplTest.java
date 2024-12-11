package home.project.service.order;

import home.project.domain.delivery.Shipping;
import home.project.domain.elasticsearch.OrdersDocument;
import home.project.domain.member.Member;
import home.project.domain.order.Orders;
import home.project.domain.product.ProductOrder;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.responseDTO.OrderResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.member.MemberRepository;
import home.project.repository.order.OrderRepository;
import home.project.repositoryForElasticsearch.OrdersElasticsearchRepository;
import home.project.service.member.MemberService;
import home.project.service.product.ProductService;
import home.project.service.util.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private ProductService productService;
    @Mock
    private OrdersElasticsearchRepository ordersElasticsearchRepository;
    @Mock
    private Converter converter;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    private CreateOrderRequestDTO createOrderRequestDTO;
    private Orders testOrder;
    private Member testMember;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock Authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 기본 테스트 데이터
        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@example.com");
        testMember.setAccumulatedPurchase(0L);

        testOrder = new Orders();
        testOrder.setId(1L);
        testOrder.setOrderNum("TEST123456");
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setMember(testMember);
        testOrder.setProductOrders(new ArrayList<>());

        createOrderRequestDTO = new CreateOrderRequestDTO();
        createOrderRequestDTO.setPointsUsed(100L);
        createOrderRequestDTO.setCouponId(null);
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrderSuccess() {
        // given
        when(authentication.getName()).thenReturn("test@example.com");
        when(memberService.findByEmail(anyString())).thenReturn(testMember);
        when(orderRepository.save(any(Orders.class))).thenReturn(testOrder);
        when(converter.convertFromOrderToOrderResponse(any(Orders.class))).thenReturn(new OrderResponse(
                1L,                        // id
                "TEST123456",              // orderNum
                LocalDateTime.now(),       // orderDate
                "test address",            // deliveryAddress
                10000L,                    // totalAmount
                500L,                      // pointsUsed
                100L,                      // pointsEarned
                new ArrayList<>()          // products
        ));

        // when
        OrderResponse response = orderService.join(createOrderRequestDTO);

        // then
        assertThat(response).isNotNull();
        verify(orderRepository, times(1)).save(any(Orders.class));
        verify(memberRepository, times(1)).save(testMember);
    }

    @Test
    @DisplayName("주문 조회 실패 - 존재하지 않는 주문")
    void findByIdOrderNotFound() {
        // given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(IdNotFoundException.class, () -> orderService.findById(1L));
    }

    @Test
    @DisplayName("주문 삭제 성공")
    void deleteOrderSuccess() {
        // given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // when
        String result = orderService.deleteById(1L);

        // then
        assertThat(result).isEqualTo("TEST123456");
        verify(orderRepository, times(1)).deleteById(1L);
        verify(memberRepository, times(1)).save(testMember);
    }

    @Test
    @DisplayName("주문 삭제 실패 - 존재하지 않는 주문")
    void deleteOrderFailOrderNotFound() {
        // given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(IdNotFoundException.class, () -> orderService.deleteById(1L));
    }

    @Test
    @DisplayName("주문 목록 조회 성공")
    void findAllOrdersSuccess() {
        // given
        Page<Orders> mockPage = mock(Page.class);
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(mockPage);
        when(converter.convertFromPagedOrderToPagedOrderResponse(any(Page.class))).thenReturn(mock(Page.class));

        // when
        Page<OrderResponse> result = orderService.findAll(Pageable.unpaged());

        // then
        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("주문 검색 성공 - Elasticsearch")
    void searchOrdersWithElasticsearch() {
        // given
        Page<OrdersDocument> mockDocumentPage = mock(Page.class);
        when(ordersElasticsearchRepository.findOrders(anyString(), anyString(), anyString(), anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockDocumentPage);

        Page<Orders> mockOrderPage = mock(Page.class);
        when(converter.convertFromPagedOrderToPagedOrderResponse(any(Page.class))).thenReturn(mock(Page.class));

        // when
        Page<OrderResponse> result = orderService.findOrders("TEST", "2023-01-01", "123", "test@example.com", "content", Pageable.unpaged());

        // then
        assertThat(result).isNotNull();
        verify(ordersElasticsearchRepository, times(1)).findOrders(anyString(), anyString(), anyString(), anyString(), anyString(), any(Pageable.class));
    }
}
