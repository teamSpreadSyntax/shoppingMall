package home.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.*;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.requestDTO.CreateShippingRequestDTO;
import home.project.dto.requestDTO.ProductDTOForOrder;
import home.project.dto.responseDTO.OrderResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final ProductService productService;
//    private final MemberOrderRepository memberOrderRepository;
//    private final ProductOrderRepository productOrderRepository;
    private final MemberRepository memberRepository;
//    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Converter converter;


    @Override
    @Transactional
    public OrderResponse join(CreateOrderRequestDTO createOrderRequestDTO){

        Shipping shipping = converter.convertFromCreateShippingRequestDTOToShipping(createOrderRequestDTO);

        Orders orders = new Orders();
        LocalDateTime orderDate = LocalDateTime.now();
        orders.setOrderNum(generateOrderNumber(createOrderRequestDTO.getProductOrders(), orderDate));

        orders.setOrderDate(orderDate);
        orders.setShipping(shipping);
        shipping.setOrders(orders); // 양방향 관계 설정

        long amount = 0L;


        for (ProductDTOForOrder productDTO : createOrderRequestDTO.getProductOrders()) {
            Product product = productService.findById(productDTO.getProductId());

            productService.decreaseStock(productDTO.getProductId(), productDTO.getQuantity().longValue());
            productService.increaseSoldQuantity(productDTO.getProductId(), productDTO.getQuantity().longValue());


            ProductOrder productOrder = new ProductOrder();
            productOrder.setProduct(product);
            productOrder.setQuantity(productDTO.getQuantity());
            productOrder.setPrice(productDTO.getPrice());
            productOrder.setOrders(orders);
            orders.getProductOrders().add(productOrder);

            amount += productDTO.getPrice()*productDTO.getQuantity();
        }
        orders.setAmount(amount);

        Member member = memberService.findByEmail(createOrderRequestDTO.getEmail());
        long newAccumulatePurchase = member.getAccumulatedPurchase()+amount;
        if (newAccumulatePurchase < 0) {
            member.setGrade(MemberGrade.BRONZE);
        } else if (newAccumulatePurchase >= 10000 && newAccumulatePurchase < 200000) {
            member.setGrade(MemberGrade.SILVER);
        } else if (newAccumulatePurchase >= 200000 && newAccumulatePurchase < 300000) {
            member.setGrade(MemberGrade.GOLD);
        } else if (newAccumulatePurchase >= 300000) {
            member.setGrade(MemberGrade.PLATINUM);
        }
        member.setAccumulatedPurchase(newAccumulatePurchase);
        orders.setMember(member);
        memberRepository.save(member);
        orderRepository.save(orders);

        return converter.convertFromOrderToOrderResponse(orders);
    }

    private String generateOrderNumber(List<ProductDTOForOrder> orderItems, LocalDateTime orderDate) {
        String productPrefix = orderItems.stream()
                .map(item -> String.valueOf(item.getProductId()))
                .map(id -> id.length() > 0 ? id.substring(0, 1) : "")                .collect(Collectors.joining());
        String orderDateString = orderDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return productPrefix + orderDateString;
    }

    @Override
    public Page<OrderResponse> findAll(Pageable pageable) {
        Page<Orders> pagedOrder= orderRepository.findAll(pageable);
        return converter.convertFromPagedOrderToPagedOrderResponse(pagedOrder);
    }

    @Override
    public OrderResponse findByIdReturnOrderResponse(Long orderId) {
        return converter.convertFromOrderToOrderResponse(findById(orderId));
    }

    @Override
    public Orders findById(Long orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IdNotFoundException(orderId + "(으)로 등록된 주문이 없습니다."));
    }

    @Override
    public Page<OrderResponse> findOrders(String orderNum, String orderDate, String productNumber, String email, String content, Pageable pageable) {

        Page<Orders> pagedOrder = orderRepository.findOrders(orderNum, orderDate, productNumber, email, content, pageable);

        return converter.convertFromPagedOrderToPagedOrderResponse(pagedOrder);
    }

    @Override
    @Transactional
    public String deleteById(Long orderId) {
        Orders order = findById(orderId);
        String orderNum = order.getOrderNum();

        Member member = order.getMember();
        long orderAmount = order.getAmount();

        long newAccumulatedPurchase = Math.max(member.getAccumulatedPurchase() - orderAmount, 0);
        member.setAccumulatedPurchase(newAccumulatedPurchase);

        if (newAccumulatedPurchase < 10000) {
            member.setGrade(MemberGrade.BRONZE);
        } else if (newAccumulatedPurchase < 200000) {
            member.setGrade(MemberGrade.SILVER);
        } else if (newAccumulatedPurchase < 300000) {
            member.setGrade(MemberGrade.GOLD);
        } else {
            member.setGrade(MemberGrade.PLATINUM);
        }

        for (ProductOrder productOrder : order.getProductOrders()) {
            Product product = productOrder.getProduct();
            int quantity = productOrder.getQuantity();
            productService.increaseStock(product.getId(), (long) quantity);
            productService.decreaseSoldQuantity(product.getId(), (long) quantity);

        }

        orderRepository.deleteById(orderId);
        memberRepository.save(member);

        return orderNum;
    }



}
