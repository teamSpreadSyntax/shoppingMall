package home.project.service.order;

import home.project.domain.common.WishList;
import home.project.domain.member.Member;
import home.project.domain.product.Product;
import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.WishListResponse;
import home.project.repository.product.WishListRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishListServiceImplTest {

    @Mock
    private WishListRepository wishListRepository;

    @Mock
    private ProductService productService;

    @Mock
    private MemberService memberService;

    @Mock
    private Converter converter;

    @InjectMocks
    private WishListServiceImpl wishListService;

    private Member testMember;
    private Product testProduct;
    private WishList testWishList;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@example.com");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        testWishList = new WishList();
        testWishList.setId(1L);
        testWishList.setMember(testMember);
        testWishList.setProduct(testProduct);
        testWishList.setLiked(true);
        testWishList.setCreateAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("위시리스트 추가 테스트")
    class AddToWishListTest {
        @Test
        @DisplayName("신규 상품 위시리스트 추가 성공")
        void addToWishListSuccess() {
            // given
            WishList savedWishList = new WishList();
            savedWishList.setId(1L);
            savedWishList.setMember(testMember);
            savedWishList.setProduct(testProduct);
            savedWishList.setLiked(true);
            savedWishList.setCreateAt(LocalDateTime.now());

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(wishListRepository.findByMemberIdAndProductId(anyLong(), anyLong())).thenReturn(null);

            when(wishListRepository.save(any(WishList.class))).thenAnswer(invocation -> {
                WishList wishList = invocation.getArgument(0);
                wishList.setId(1L);
                return wishList;
            });

            WishListResponse response = wishListService.addToWishList(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getProductId()).isEqualTo(testProduct.getId());
            assertThat(response.isLiked()).isTrue();
            assertThat(response.getMessage()).isEqualTo("위시리스트에 추가되었습니다.");

            verify(memberService).findByEmail(anyString());
            verify(productService).findById(1L);
            verify(wishListRepository).findByMemberIdAndProductId(anyLong(), anyLong());
            verify(wishListRepository).save(any(WishList.class));
        }

        @Test
        @DisplayName("이미 존재하는 상품 위시리스트 추가 시도")
        void addToWishListAlreadyExists() {
            // given
            testWishList.setLiked(true);
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(wishListRepository.findByMemberIdAndProductId(anyLong(), anyLong())).thenReturn(testWishList);

            // when
            WishListResponse response = wishListService.addToWishList(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("이미 위시리스트에 존재합니다.");
            assertThat(response.getId()).isEqualTo(testWishList.getId());
            assertThat(response.isLiked()).isTrue();

            verify(wishListRepository, never()).save(any());
        }

        @Test
        @DisplayName("상품이 존재하지 않는 경우 추가 실패")
        void addToWishListProductNotFound() {
            // given
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong()))
                    .thenThrow(new IllegalArgumentException("상품을 찾을 수 없습니다."));

            // when & then
            assertThatThrownBy(() -> wishListService.addToWishList(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상품을 찾을 수 없습니다.");

            verify(wishListRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("위시리스트 삭제 테스트")
    class RemoveFromWishListTest {
        @Test
        @DisplayName("위시리스트에서 상품 삭제 성공")
        void removeFromWishListSuccess() {
            // given
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(wishListRepository.findByMemberIdAndProductId(anyLong(), anyLong())).thenReturn(testWishList);

            // when
            WishListResponse response = wishListService.removeFromWishList(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("위시리스트에서 삭제되었습니다.");
            assertThat(response.getId()).isEqualTo(testWishList.getId());
            assertThat(response.isLiked()).isFalse();

            verify(wishListRepository).delete(testWishList);
        }

        @Test
        @DisplayName("위시리스트에 없는 상품 삭제 시도")
        void removeFromWishListNotFound() {
            // given
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(wishListRepository.findByMemberIdAndProductId(anyLong(), anyLong())).thenReturn(null);

            // when
            WishListResponse response = wishListService.removeFromWishList(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("위시리스트에 존재하지 않습니다.");
            assertThat(response.getId()).isNull();
            assertThat(response.isLiked()).isFalse();

            verify(wishListRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("위시리스트 조회 테스트")
    class FindWishListTest {
        @Test
        @DisplayName("내 위시리스트 조회 성공")
        void findAllMyWishListSuccess() {
            // given
            Page<WishList> pagedWishList = new PageImpl<>(List.of(testWishList));
            ProductResponse expectedResponse = new ProductResponse(
                    1L, "Test Product", "Test Brand", "Category",
                    "P12345", 10000L, 10, List.of("Description"),
                    "image_url", true, "M", "Red", List.of()
            );
            Page<ProductResponse> expectedPage = new PageImpl<>(List.of(expectedResponse));

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(wishListRepository.findAllByMemberId(anyLong(), any(Pageable.class))).thenReturn(pagedWishList);
            when(converter.convertFromPagedWishListToProductResponseResponse(any())).thenReturn(expectedPage);

            // when
            Page<ProductResponse> response = wishListService.findAllMyWishList(PageRequest.of(0, 10));

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getId()).isEqualTo(testProduct.getId());
            assertThat(response.getContent().get(0).getName()).isEqualTo("Test Product");

            verify(converter).convertFromPagedWishListToProductResponseResponse(pagedWishList);
        }

        @Test
        @DisplayName("빈 위시리스트 조회")
        void findEmptyWishListSuccess() {
            // given
            Page<WishList> emptyPage = new PageImpl<>(List.of());
            Page<ProductResponse> emptyResponse = new PageImpl<>(List.of());

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(wishListRepository.findAllByMemberId(anyLong(), any(Pageable.class))).thenReturn(emptyPage);
            when(converter.convertFromPagedWishListToProductResponseResponse(any())).thenReturn(emptyResponse);

            // when
            Page<ProductResponse> response = wishListService.findAllMyWishList(PageRequest.of(0, 10));

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).isEmpty();
            assertThat(response.getTotalElements()).isZero();
        }
    }
}

