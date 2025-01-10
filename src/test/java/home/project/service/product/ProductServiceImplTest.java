/*
package home.project.service.product;

import home.project.domain.member.Member;
import home.project.domain.product.Product;
import home.project.domain.product.Category;
import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.ProductResponseForManager;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.common.QnARepository;
import home.project.repository.common.ReviewRepository;
import home.project.repository.member.MemberProductRepository;
import home.project.repository.order.ProductOrderRepository;
import home.project.repository.product.CategoryRepository;
import home.project.repository.product.ProductRepository;
import home.project.repository.product.WishListRepository;
import home.project.repositoryForElasticsearch.ProductElasticsearchRepository;
import home.project.service.member.MemberService;
import home.project.service.util.Converter;
import home.project.service.util.IndexToElasticsearch;
import home.project.service.util.PageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductOrderRepository productOrderRepository;
    @Mock
    private ProductElasticsearchRepository productElasticsearchRepository;
    @Mock
    private Converter converter;
    @Mock
    private IndexToElasticsearch indexToElasticsearch;
    @Mock
    private MemberService memberService;
    @Mock
    private MemberProductRepository memberProductRepository;
    @Mock
    private WishListRepository wishListRepository;
    @Mock
    private PageUtil pageUtil;
    @Mock
    private QnARepository qnARepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private Category testCategory;
    private Member testMember;
    private CreateProductRequestDTO createProductRequestDTO;
    private UpdateProductRequestDTO updateProductRequestDTO;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        // 테스트용 데이터 초기화
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setCode("01");
        testCategory.setName("TestCategory");

        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@example.com");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("TestProduct");
        testProduct.setBrand("TestBrand");
        testProduct.setCategory(testCategory);
        testProduct.setStock(100L);
        testProduct.setPrice(10000L);
        testProduct.setProductNum("010123456789");

        createProductRequestDTO = new CreateProductRequestDTO();
        createProductRequestDTO.setName("TestProduct");
        createProductRequestDTO.setBrand("TestBrand");
        createProductRequestDTO.setCategory("01");
        createProductRequestDTO.setStock(100L);
        createProductRequestDTO.setPrice(10000L);

        updateProductRequestDTO = new UpdateProductRequestDTO();
        updateProductRequestDTO.setId(1L);
        updateProductRequestDTO.setName("UpdatedProduct");
        updateProductRequestDTO.setBrand("UpdatedBrand");
        updateProductRequestDTO.setPrice(20000L);

        // ProductResponse 객체 초기화 추가
        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("UpdatedProduct");
        productResponse.setBrand("UpdatedBrand");
        productResponse.setPrice(20000L);
    }

    @Nested
    @DisplayName("상품 생성 테스트")
    class CreateProductTest {

        @Test
        @DisplayName("정상적으로 상품을 생성한다")
        void createProductSuccess() {
            // given
            when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(testCategory));
            when(productRepository.existsByProductNum(anyString())).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);
            when(memberService.findByEmail(anyString())).thenReturn(testMember);

            // when
            productService.join(createProductRequestDTO);

            // then
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("카테고리가 존재하지 않을 경우 실패한다")
        void createProductFailNoCategory() {
            // given
            when(categoryRepository.findByCode(anyString())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.join(createProductRequestDTO))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("카테고리가 없습니다");
        }
    }

    @Nested
    @DisplayName("상품 조회 테스트")
    class FindProductTest {

        @Test
        @DisplayName("ID로 상품을 조회한다")
        void findByIdSuccess() {
            // given
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

            // when
            Product product = productService.findById(1L);

            // then
            assertThat(product).isNotNull();
            assertThat(product.getName()).isEqualTo("TestProduct");
            verify(productRepository).findById(anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID로 조회할 경우 실패한다")
        void findByIdFailNotFound() {
            // given
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.findById(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 상품이 없습니다");
        }
    }

    @Nested
    @DisplayName("상품 업데이트 테스트")
    class UpdateProductTest {

        @Test
        @DisplayName("상품 정보를 업데이트한다")
        void updateProductSuccess() {
            // given
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
            when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(testCategory));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);
            when(converter.convertFromProductToProductResponse(any(Product.class))).thenReturn(productResponse);

            // when
            ProductResponse response = productService.update(updateProductRequestDTO);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("UpdatedProduct");
            assertThat(response.getBrand()).isEqualTo("UpdatedBrand");
            assertThat(response.getPrice()).isEqualTo(20000L);
            verify(productRepository).save(any(Product.class));
            verify(converter).convertFromProductToProductResponse(any(Product.class));
        }
    }

    @Nested
    @DisplayName("상품 삭제 테스트")
    class DeleteProductTest {

        @Test
        @DisplayName("상품을 삭제한다")
        void deleteProductSuccess() {
            // given
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

            // when
            String deletedProductName = productService.deleteById(1L);

            // then
            assertThat(deletedProductName).isEqualTo("TestProduct");
            verify(productRepository).deleteById(anyLong());
        }
    }
}*/
