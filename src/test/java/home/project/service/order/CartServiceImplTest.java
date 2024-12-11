package home.project.service.order;

import home.project.domain.member.Member;
import home.project.domain.order.Cart;
import home.project.domain.product.Product;
import home.project.domain.product.ProductCart;
import home.project.dto.requestDTO.ProductDTOForOrder;
import home.project.dto.responseDTO.CartResponse;
import home.project.dto.responseDTO.MyCartResponse;
import home.project.repository.order.CartRepository;
import home.project.service.member.MemberService;
import home.project.service.product.ProductService;
import home.project.service.util.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ProductService productService;

    @Mock
    private Converter converter;

    @InjectMocks
    private CartServiceImpl cartService;

    private Member testMember;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        // 인증 객체 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@test.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 테스트 데이터 초기화
        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@test.com");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        ProductCart productCart = new ProductCart();
        productCart.setProduct(testProduct);
        productCart.setQuantity(2);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setMember(testMember);
        testCart.setProductCart(Collections.singletonList(productCart));
    }

    @Nested
    @DisplayName("장바구니 추가")
    class AddToCartTest {

        @Test
        @DisplayName("장바구니 추가 성공")
        void addToCartSuccess() {
            // given
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            ProductDTOForOrder productDTOForOrder = new ProductDTOForOrder(1L, 10000L, 2);
            List<ProductDTOForOrder> productDTOForOrderList = List.of(productDTOForOrder);

            when(converter.convertFromCartToCartResponse(any(Cart.class)))
                    .thenReturn(new CartResponse("test@test.com", productDTOForOrderList));

            // when
            CartResponse response = cartService.join(1L, 2);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo("test@test.com");
            assertThat(response.getProducts()).hasSize(1);
            assertThat(response.getProducts().get(0).getQuantity()).isEqualTo(2);

            verify(cartRepository).save(any(Cart.class));
        }
    }

    /*@Nested
    @DisplayName("회원별 장바구니 조회")
    class FindAllByMemberIdTest {

        @Test
        @DisplayName("회원별 장바구니 조회 성공")
        void findAllByMemberIdSuccess() {
            // given
            Page<Cart> pagedCart = new PageImpl<>(Collections.singletonList(testCart));

            // ProductDTOForOrder 객체 생성 (테스트용 데이터)
            ProductDTOForOrder productDTOForOrder = new ProductDTOForOrder(1L, 10000L, 2);
            List<ProductDTOForOrder> productList = Collections.singletonList(productDTOForOrder);

            // MyCartResponse 객체 생성
            Page<MyCartResponse> expectedResponses = new PageImpl<>(Collections.singletonList(
                    new MyCartResponse(productList)  // MyCartResponse 생성자에 맞게 전달
            ));

            // Mock 설정
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(cartRepository.findAllByMemberId(anyLong(), any(Pageable.class))).thenReturn(pagedCart);
            when(converter.convertFromPagedCartToPagedMyCartResponse(any(Page.class))).thenReturn(expectedResponses);

            // when
            Page<MyCartResponse> responses = cartService.findAllByMemberId(Pageable.unpaged());

            // then
            assertThat(responses).isNotNull();
            assertThat(responses.getContent()).hasSize(1);
            assertThat(responses.getContent().get(0).getProducts()).isNotNull();
            assertThat(responses.getContent().get(0).getProducts().get(0).getQuantity()).isEqualTo(2);

            verify(cartRepository).findAllByMemberId(anyLong(), any(Pageable.class));
        }*/

        @Nested
        @DisplayName("장바구니 삭제")
        class DeleteCartTest {

            @Test
            @DisplayName("장바구니 삭제 성공")
            void deleteCartSuccess() {
                // given
                when(productService.findById(anyLong())).thenReturn(testProduct);

                // when
                String response = cartService.deleteById(1L);

                // then
                assertThat(response).isEqualTo("Test Product");
                verify(cartRepository).deleteById(anyLong());
            }
        }
    }
