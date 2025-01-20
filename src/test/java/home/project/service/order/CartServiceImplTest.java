package home.project.service.order;

import home.project.domain.member.Member;
import home.project.domain.order.Cart;
import home.project.domain.product.Product;
import home.project.domain.product.ProductCart;
import home.project.dto.responseDTO.CartResponse;
import home.project.dto.responseDTO.ProductSimpleResponseForCart;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.order.CartRepository;
import home.project.repository.order.ProductCartRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductCartRepository productCartRepository;

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
    private ProductCart testProductCart;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@test.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@test.com");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        testProductCart = new ProductCart();
        testProductCart.setProduct(testProduct);
        testProductCart.setQuantity(2);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setMember(testMember);
        testCart.setProductCart(Collections.singletonList(testProductCart));
    }

    @Nested
    @DisplayName("장바구니 추가")
    class AddToCartTest {

        @Test
        @DisplayName("장바구니 추가 성공")
        void addToCartSuccess() {
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
            when(converter.convertFromCartToCartResponse(any(Cart.class))).thenReturn(new CartResponse("test@test.com", null));

            CartResponse response = cartService.join(1L, 2);

            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo("test@test.com");
            verify(cartRepository).save(any(Cart.class));
        }

        @Test
        @DisplayName("장바구니 추가 실패 - 상품 없음")
        void addToCartFailProductNotFound() {
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenThrow(new IdNotFoundException("상품이 존재하지 않습니다."));

            assertThatThrownBy(() -> cartService.join(1L, 2))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("상품이 존재하지 않습니다.");

            verify(cartRepository, never()).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("장바구니 조회")
    class FindAllByMemberIdTest {

        @Test
        @DisplayName("장바구니 조회 성공")
        void findAllByMemberIdSuccess() {
            Page<ProductCart> pagedProductCart = new PageImpl<>(Collections.singletonList(testProductCart));
            Page<ProductSimpleResponseForCart> expectedResponse = new PageImpl<>(List.of(
                    new ProductSimpleResponseForCart(
                            1L,
                            "Test Product",
                            "Test Brand",
                            10000L,
                            10,
                            "image_url",
                            2,
                            "Blue"
                    )
            ));
            Pageable pageable = PageRequest.of(0, 10);

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productCartRepository.findByCart_Member_Id(anyLong(), any(Pageable.class))).thenReturn(pagedProductCart);
            when(converter.convertFromListedProductCartToPagedProductSimpleResponseForCart(any(Page.class)))
                    .thenReturn(expectedResponse);

            Page<ProductSimpleResponseForCart> response = cartService.findAllByMemberId(pageable);

            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getName()).isEqualTo("Test Product");
        }

        @Test
        @DisplayName("장바구니 조회 실패 - 회원 없음")
        void findAllByMemberIdFailMemberNotFound() {
            when(memberService.findByEmail(anyString())).thenThrow(new IdNotFoundException("회원이 존재하지 않습니다."));

            assertThatThrownBy(() -> cartService.findAllByMemberId(Pageable.unpaged()))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("회원이 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("장바구니에서 상품 삭제")
    class DeleteByProductIdTest {

        @Test
        @DisplayName("장바구니에서 상품 삭제 성공")
        void deleteByProductIdSuccess() {
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);

            String response = cartService.deleteByProductId(1L);

            assertThat(response).isEqualTo("Test Product");
            verify(productCartRepository).deleteByProductIdAndCart_MemberId(anyLong(), anyLong());
        }

        @Test
        @DisplayName("장바구니에서 상품 삭제 실패 - 상품 없음")
        void deleteByProductIdFailProductNotFound() {
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenThrow(new IdNotFoundException("상품이 존재하지 않습니다."));

            assertThatThrownBy(() -> cartService.deleteByProductId(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("상품이 존재하지 않습니다.");

            verify(productCartRepository, never()).deleteByProductIdAndCart_MemberId(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("장바구니 ID로 조회")
    class FindByIdTest {

        @Test
        @DisplayName("장바구니 ID로 조회 성공")
        void findByIdSuccess() {
            when(cartRepository.findById(anyLong())).thenReturn(Optional.of(testCart));

            Cart cart = cartService.findById(1L);

            assertThat(cart).isNotNull();
            assertThat(cart.getId()).isEqualTo(1L);
            verify(cartRepository).findById(anyLong());
        }

        @Test
        @DisplayName("장바구니 ID로 조회 실패 - ID 없음")
        void findByIdFail() {
            when(cartRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.findById(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("1(으)로 등록된 상품이 없습니다.");
        }
    }
}
