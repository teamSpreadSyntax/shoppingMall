package home.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.*;
import home.project.dto.requestDTO.CreateCartRequestDTO;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.requestDTO.ProductDTOForOrder;
import home.project.dto.responseDTO.CartResponse;
import home.project.dto.responseDTO.OrderResponse;
import home.project.dto.responseDTO.ProductEventResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.InvalidCouponException;
import home.project.repository.*;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final ProductService productService;
//    private final MemberOrderRepository memberOrderRepository;
//    private final ProductOrderRepository productOrderRepository;
    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CouponService couponService;
    private final ShippingRepository shippingRepository;
//    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Converter converter;


    @Override
    @Transactional
    public CartResponse join(Long productId, Integer quantity){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Cart cart = new Cart();
        cart.setMember(member);


        Product product = productService.findById(productId);

//        List<ProductCart> listedProductCart = converter.convertFromListedProductDTOForOrderToListedProductCart(product, cart);
       ProductCart productCart = new ProductCart();
       productCart.setCart(cart);
       productCart.setProduct(product);
       productCart.setQuantity(quantity);

        List<ProductCart> listedProductCart = new ArrayList<>();
        listedProductCart.add(productCart);  // set 대신 add 사용
        cart.setProductCart(listedProductCart);

        cartRepository.save(cart);

        return converter.convertFromCartToCartResponse(cart);
    }


    @Override
    public Cart findById(Long cartId) {
        if (cartId == null) {
            throw new IllegalStateException("id가 입력되지 않았습니다.");
        }

        return cartRepository.findById(cartId)
                .orElseThrow(() -> new IdNotFoundException(cartId + "(으)로 등록된 상품이 없습니다."));
    }

    @Override
    public Page<CartResponse> findAllByMemberId(Pageable pageable){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long memberId = memberService.findByEmail(email).getId();
        Page<Cart> pagedCart = cartRepository.findAllByMemberId(memberId, pageable);
        return converter.convertFromPagedCartToPagedCartResponse(pagedCart);
    }
    @Override
    @Transactional
    public String deleteById(Long productId) {
        String name = productService.findById(productId).getName();
        cartRepository.deleteById(productId);
        return name;
    }



}
