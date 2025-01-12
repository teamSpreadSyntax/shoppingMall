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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        // 기본 테스트 데이터
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

        // SecurityContext Mock 설정
        mockSecurityContext(testMember.getEmail());
    }

    @Nested
    @DisplayName("위시리스트 추가 테스트")
    class AddToWishListTest {

        @Test
        @DisplayName("위시리스트에 상품 추가 성공")
        void addToWishListSuccess() {
            // given
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(wishListRepository.findByMemberIdAndProductId(anyLong(), anyLong())).thenReturn(null);
            when(wishListRepository.save(any(WishList.class))).thenReturn(testWishList);

            // when
            WishListResponse response = wishListService.addToWishList(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("위시리스트에 추가되었습니다.");
            verify(wishListRepository).save(any(WishList.class));
        }

        @Test
        @DisplayName("위시리스트에 이미 존재하는 상품 추가 시도")
        void addToWishListAlreadyExists() {
            // given
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findById(anyLong())).thenReturn(testProduct);
            when(wishListRepository.findByMemberIdAndProductId(anyLong(), anyLong())).thenReturn(testWishList);

            // when
            WishListResponse response = wishListService.addToWishList(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("이미 위시리스트에 존재합니다.");
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
            verify(wishListRepository).delete(any(WishList.class));
        }

        @Test
        @DisplayName("위시리스트에 존재하지 않는 상품 삭제 시도")
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
            verify(wishListRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("위시리스트 조회 테스트")
    class FindAllMyWishListTest {

        @Test
        @DisplayName("위시리스트 조회 성공")
        void findAllMyWishListSuccess() {
            // given
            Page<WishList> pagedWishList = new PageImpl<>(List.of(testWishList));
            Page<ProductResponse> pagedProductResponse = new PageImpl<>(List.of(
                    new ProductResponse(
                            1L, "Test Product", "Test Brand", "Category",
                            "P12345", 10000L, 10, List.of("Description"),
                            "image_url", true, "M", "Red", List.of()
                    )
            ));
            Pageable pageable = PageRequest.of(0, 10);

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(wishListRepository.findAllByMemberId(anyLong(), any(Pageable.class))).thenReturn(pagedWishList);
            when(converter.convertFromPagedWishListToProductResponseResponse(any(Page.class))).thenReturn(pagedProductResponse);

            // when
            Page<ProductResponse> response = wishListService.findAllMyWishList(pageable);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            verify(wishListRepository).findAllByMemberId(anyLong(), any(Pageable.class));
            verify(converter).convertFromPagedWishListToProductResponseResponse(any(Page.class));
        }
    }

    private void mockSecurityContext(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }
}

