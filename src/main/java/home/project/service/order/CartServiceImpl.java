package home.project.service.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.member.Member;
import home.project.domain.order.Cart;
import home.project.domain.product.Product;
import home.project.domain.product.ProductCart;
import home.project.dto.requestDTO.ProductDTOForOrder;
import home.project.dto.responseDTO.CartResponse;
import home.project.dto.responseDTO.MyCartResponse;
import home.project.dto.responseDTO.ProductSimpleResponseForCart;
import home.project.dto.responseDTO.ProductSimpleResponsesForCart;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.member.MemberRepository;
import home.project.repository.order.CartRepository;
import home.project.repository.order.OrderRepository;
import home.project.repository.order.ProductCartRepository;
import home.project.repository.promotion.MemberCouponRepository;
import home.project.repository.shipping.ShippingRepository;
import home.project.service.member.MemberService;
import home.project.service.product.ProductService;
import home.project.service.promotion.CouponService;
import home.project.service.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final Converter converter;
    private final ProductCartRepository productCartRepository;


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
    public Page<ProductSimpleResponseForCart> findAllByMemberId(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long memberId = memberService.findByEmail(email).getId();

        Page<ProductCart> productCarts = productCartRepository.findByCart_Member_Id(memberId, pageable);
        return converter.convertFromListedProductCartToPagedProductSimpleResponseForCart(productCarts);
    }

    @Override
    @Transactional
    public String deleteByProductId(Long productId) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long memberId = memberService.findByEmail(email).getId();

        String name = productService.findById(productId).getName();
        productCartRepository.deleteByProductIdAndCart_MemberId(productId, memberId);
        return name;
    }



}
