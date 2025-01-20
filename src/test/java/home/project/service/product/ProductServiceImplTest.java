package home.project.service.product;

import home.project.config.TestConfig;
import home.project.domain.elasticsearch.ProductDocument;
import home.project.domain.member.Member;
import home.project.domain.product.Category;
import home.project.domain.product.Product;
import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import home.project.dto.responseDTO.ProductResponse;
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
import home.project.service.file.FileService;
import home.project.service.integration.IndexToElasticsearch;
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
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
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
    @Mock
    private FileService fileService;

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
        // 테스트 데이터 초기화
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
            when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(testCategory));
            when(productRepository.existsByProductNum(anyString())).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(fileService.saveFile(any(), anyString(), anyString())).thenReturn("test-image-url");

            MultipartFile mockFile = mock(MultipartFile.class);
            when(mockFile.isEmpty()).thenReturn(false);

            productService.join(createProductRequestDTO, mockFile, List.of());

            verify(productRepository).save(any(Product.class));
            verify(fileService, times(1)).saveFile(any(), anyString(), anyString());
        }

        @Test
        @DisplayName("카테고리가 존재하지 않을 경우 실패한다")
        void createProductFailNoCategory() {
            when(categoryRepository.findByCode(anyString())).thenReturn(Optional.empty());
            when(memberService.findByEmail(anyString())).thenReturn(testMember); // Mock Member

            MultipartFile mockFile = mock(MultipartFile.class);
            when(mockFile.isEmpty()).thenReturn(false);

            assertThatThrownBy(() -> productService.join(createProductRequestDTO, mockFile, List.of()))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("카테고리가 없습니다");
        }

        @Test
        @DisplayName("대표 이미지 파일이 없을 경우 실패한다")
        void createProductFailNoImage() {
            when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(testCategory));

            MultipartFile mockFile = null;

            assertThatThrownBy(() -> productService.join(createProductRequestDTO, mockFile, List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("대표 이미지 파일은 반드시 포함되어야 합니다.");
        }
    }

    @Nested
    @DisplayName("상품 조회 테스트")
    class FindProductTest {

        @Test
        @DisplayName("ID로 상품을 조회한다")
        void findByIdSuccess() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

            Product product = productService.findById(1L);

            assertThat(product).isNotNull();
            assertThat(product.getName()).isEqualTo("TestProduct");
            verify(productRepository).findById(anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID로 조회할 경우 실패한다")
        void findByIdFailNotFound() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

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
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
            when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(testCategory));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);
            when(converter.convertFromProductToProductResponse(any(Product.class))).thenReturn(productResponse);
            when(fileService.saveFile(any(), anyString(), anyString())).thenReturn("updated-image-url");

            ProductResponse response = productService.update(updateProductRequestDTO, null, List.of());

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("UpdatedProduct");
            assertThat(response.getBrand()).isEqualTo("UpdatedBrand");
            assertThat(response.getPrice()).isEqualTo(20000L);
            verify(productRepository).save(any(Product.class));
            verify(converter).convertFromProductToProductResponse(any(Product.class));
        }

        @Test
        @DisplayName("존재하지 않는 상품 업데이트 시 실패한다")
        void updateProductFailNoProduct() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.update(updateProductRequestDTO, null, List.of()))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 상품이 없습니다");
        }
    }

    @Nested
    @DisplayName("상품 삭제 테스트")
    class DeleteProductTest {

        @Test
        @DisplayName("상품을 삭제한다")
        void deleteProductSuccess() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

            String deletedProductName = productService.deleteById(1L);

            assertThat(deletedProductName).isEqualTo("TestProduct");
            verify(productRepository).deleteById(anyLong());
            verify(elasticsearchOperations).delete(anyString(), eq(ProductDocument.class));
        }

        @Test
        @DisplayName("존재하지 않는 상품 삭제 시 실패한다")
        void deleteProductFailNoProduct() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.deleteById(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 상품이 없습니다");
        }
    }
}
