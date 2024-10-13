package home.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.*;
import home.project.dto.OrderEventDTO;
import home.project.dto.requestDTO.AssignOrderToMemberRequestDTO;
import home.project.dto.requestDTO.AssignOrderToProductRequestDTO;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.responseDTO.OrderResponse;
import home.project.dto.responseDTO.MemberOrderResponse;
import home.project.dto.responseDTO.ProductOrderResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.*;
import home.project.util.StringBuilderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
//    private final MemberOrderRepository memberOrderRepository;
//    private final ProductOrderRepository productOrderRepository;
//    private final MemberRepository memberRepository;
//    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Converter converter;


    @Override
    @Transactional
    public OrderResponse join(CreateOrderRequestDTO createOrderRequestDTO){
        Order order = new Order();
        order.setO(createOrderRequestDTO.getName());
        orderRepository.save(order);

        sendOrderEvent(new OrderEventDTO("order_created", order.getId()));

        return converter.convertFromOrderToOrderResponse(order);
    }

    @Override
    public Page<OrderResponse> findAll(Pageable pageable) {
        Page<Order> pagedOrder= orderRepository.findAll(pageable);
        return converter.convertFromPagedOrderToPagedOrderResponse(pagedOrder);
    }

    @Override
    public OrderResponse findByIdReturnOrderResponse(Long orderId) {
        return converter.convertFromOrderToOrderResponse(findById(orderId));
    }


    private void sendOrderEvent(OrderEventDTO event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order-events", message);
        } catch (JsonProcessingException e) {
            // 에러 처리
            e.printStackTrace();
        }
    }

    @Override
    public Order findById(Long orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IdNotFoundException(orderId + "(으)로 등록된 쿠폰이 없습니다."));
    }

    @Override
    public Page<OrderResponse> findOrders(String name, String startDate, String endDate, String assignBy, String content, Pageable pageable) {

        Page<Order> pagedOrder = orderRepository.findOrders(name, startDate, endDate, assignBy, content, pageable);

        return converter.convertFromPagedOrderToPagedOrderResponse(pagedOrder);
    }

    @Override
    @Transactional
    public String deleteById(Long orderId) {
        String name = findById(orderId).getName();
        orderRepository.deleteById(orderId);
        return name;
    }



}
