package home.project.controller;

import home.project.SecurityConfig;
import home.project.domain.*;
import home.project.exceptions.GlobalExceptionHandler;
import home.project.service.JwtTokenProvider;
import home.project.service.ProductService;
import home.project.service.ValidationCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private ProductService productService;

    @MockBean
    private ValidationCheck validationCheck;

    @Mock
    private BindingResult bindingResult;

    private ProductDTOWithoutId productDTO;
    private Product product;
    private List<Product> products;
    private Page<Product> productPage;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        productDTO = new ProductDTOWithoutId();
        productDTO.setBrand("TestBrand");
        productDTO.setCategory("TestCategory");
        productDTO.setName("TestProduct");
        productDTO.setStock(100L);
        productDTO.setSoldQuantity(10L);
        productDTO.setImage("test.jpg");

        product = new Product();
        product.setId(1L);
        product.setBrand(productDTO.getBrand());
        product.setCategory(productDTO.getCategory());
        product.setName(productDTO.getName());
        product.setStock(productDTO.getStock());
        product.setSoldQuantity(productDTO.getSoldQuantity());
        product.setImage(productDTO.getImage());

        Product product2 = new Product();
        product2.setId(2L);
        product2.setBrand("AnotherBrand");
        product2.setCategory("AnotherCategory");
        product2.setName("AnotherProduct");
        product2.setStock(100L);
        product2.setSoldQuantity(10L);
        product2.setImage("another.jpg");

        products = Arrays.asList(product, product2);
        pageable = PageRequest.of(0, 5);
        productPage = new PageImpl<>(products, pageable, products.size());
    }

    @Nested
    class CreateProductTest {
        @Test
        public void createProduct_ValidInput_ReturnsCreatedProduct() throws Exception {
            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(null);
            doNothing().when(productService).join(any(Product.class));

            mockMvc.perform(post("/api/product/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"brand\": \"TestBrand\", \"category\": \"TestCategory\", \"name\": \"TestProduct\", \"stock\": 100, \"soldQuantity\": 0, \"image\": \"test.jpg\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.successMessage").value(product.getName() + "(이)가 등록되었습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("상품 등록 성공"))
                    .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        public void createProduct_InvalidInput_ReturnsBadRequest() throws Exception {
            Map<String, String> errors = new HashMap<>();
            errors.put("brand", "브랜드명은 필수입니다.");
            when(validationCheck.validationChecks(any(BindingResult.class)))
                    .thenReturn(new CustomOptionalResponseEntity<>(
                            new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value()),
                            HttpStatus.BAD_REQUEST
                    ));

            mockMvc.perform(post("/api/product/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"brand\": \"\", \"category\": \"TestCategory\", \"name\": \"TestProduct\", \"stock\": 100, \"soldQuantity\": 0, \"image\": \"test.jpg\" }"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.result.brand").value("브랜드명은 필수입니다."));
        }
    }

    @Nested
    class FindProductByIdTest {
        @Test
        public void findProductById_ExistingProduct_ReturnsProductInfo() throws Exception {
            when(productService.findById(1L)).thenReturn(Optional.of(product));

            mockMvc.perform(get("/api/product/product")
                            .param("productId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(product.getId()))
                    .andExpect(jsonPath("$.result.brand").value(product.getBrand()))
                    .andExpect(jsonPath("$.responseMessage").value("1에 해당하는 상품 입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        public void findProductById_NonExistingProduct_ReturnsNotFound() throws Exception {
            when(productService.findById(99L)).thenThrow(new IllegalArgumentException("99(으)로 등록된 상품이 없습니다."));

            mockMvc.perform(get("/api/product/product")
                            .param("productId", "99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.result.errorMessage").value("99(으)로 등록된 상품이 없습니다."));
        }
    }

    @Nested
    class FindAllProductsTest {
        @Test
        public void findAllProducts_ReturnsProductsPage() throws Exception {
            when(productService.findAll(any(Pageable.class))).thenReturn(productPage);

            mockMvc.perform(get("/api/product/products")
                            .param("page", "1")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(products.size()))
                    .andExpect(jsonPath("$.responseMessage").value("전체 상품입니다."))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.result.totalCount").value(products.size()))
                    .andExpect(jsonPath("$.result.page").value(0));
        }

        @Test
        public void findAllProducts_EmptyPage_ReturnsEmptyList() throws Exception {
            when(productService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

            mockMvc.perform(get("/api/product/products")
                            .param("page", "1")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(0))
                    .andExpect(jsonPath("$.responseMessage").value("전체 상품입니다."))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.result.totalCount").value(0))
                    .andExpect(jsonPath("$.result.page").value(0));
        }
    }

    @Nested
    class SearchProductsTest {
        @Test
        public void searchProducts_ValidInput_ReturnsMatchingProducts() throws Exception {
            when(productService.findProducts(anyString(), anyString(), anyString(), anyString(), any(Pageable.class))).thenReturn(productPage);

            mockMvc.perform(get("/api/product/search")
                            .param("brand", "TestBrand")
                            .param("category", "TestCategory")
                            .param("productName", "TestProduct")
                            .param("content", "TestContent")
                            .param("page", "1")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(products.size()))
                    .andExpect(jsonPath("$.responseMessage").value("검색 키워드 : TestBrand, TestCategory, TestProduct, TestContent"))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.result.totalCount").value(products.size()))
                    .andExpect(jsonPath("$.result.page").value(0));
        }

        @Test
        public void searchProducts_NoResults_ReturnsNotFound() throws Exception {
            when(productService.findProducts(anyString(), anyString(), anyString(), anyString(), any(Pageable.class)))
                    .thenThrow(new IllegalArgumentException("해당하는 상품이 없습니다."));

            mockMvc.perform(get("/api/product/search")
                    .param("brand", "TestBrand")
                    .param("category", "TestCategory")
                    .param("productName", "TestProduct")
                    .param("content", "TestContent")
                    .param("page", "1")
                    .param("size", "5"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.result.errorMessage").value("해당하는 상품이 없습니다."));
        }
    }

    @Nested
    class UpdateProductTest {
        @Test
        public void updateProduct_ValidInput_ReturnsUpdatedProduct() throws Exception {
            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(null);
            when(productService.update(any(Product.class))).thenReturn(Optional.of(product));

            mockMvc.perform(put("/api/product/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"brand\": \"UpdatedBrand\", \"category\": \"UpdatedCategory\", \"name\": \"UpdatedProduct\", \"stock\": 200, \"soldQuantity\": 50, \"image\": \"updated.jpg\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(product.getId()))
                    .andExpect(jsonPath("$.result.brand").value(product.getBrand()))
                    .andExpect(jsonPath("$.responseMessage").value("상품 정보가 수정되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        public void updateProduct_InvalidInput_ReturnsBadRequest() throws Exception {
            Map<String, String> errors = new HashMap<>();
            errors.put("stock", "재고는 0 이상이어야 합니다.");
            when(validationCheck.validationChecks(any(BindingResult.class)))
                    .thenReturn(new CustomOptionalResponseEntity<>(
                            new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value()),
                            HttpStatus.BAD_REQUEST
                    ));

            mockMvc.perform(put("/api/product/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"brand\": \"UpdatedBrand\", \"category\": \"UpdatedCategory\", \"name\": \"UpdatedProduct\", \"stock\": -1, \"soldQuantity\": 50, \"image\": \"updated.jpg\" }"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.result.stock").value("재고는 0 이상이어야 합니다."));
        }
    }

    @Nested
    class DeleteProductTest {
        @Test
        public void deleteProduct_ExistingProduct_ReturnsSuccessMessage() throws Exception {
            when(productService.findById(1L)).thenReturn(Optional.of(product));
            doNothing().when(productService).deleteById(1L);

            mockMvc.perform(delete("/api/product/delete")
                            .param("productId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.successMessage").value(product.getName() + "(id:1)(이)가 삭제되었습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("상품 삭제 성공"))
                    .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        public void deleteProduct_NonExistingProduct_ReturnsNotFound() throws Exception {
            when(productService.findById(99L)).thenThrow(new IllegalArgumentException("99(으)로 등록된 상품이 없습니다."));

            mockMvc.perform(delete("/api/product/delete")
                            .param("productId", "99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.result.errorMessage").value("99(으)로 등록된 상품이 없습니다."));
        }
    }

    @Nested
    class IncreaseStockTest {
        @Test
        public void increaseStock_ValidInput_ReturnsUpdatedStock() throws Exception {
            Product updatedProduct = new Product();
            updatedProduct.setId(1L);
            updatedProduct.setName("TestProduct");
            updatedProduct.setStock(150L);

            when(productService.increaseStock(1L, 50L)).thenReturn(updatedProduct);

            mockMvc.perform(put("/api/product/increase_stock")
                            .param("productId", "1")
                            .param("stock", "50"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.stock").value(150))
                    .andExpect(jsonPath("$.responseMessage").value("TestProduct상품이 50개 증가하여 150개가 되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        public void increaseStock_NegativeStock_ReturnsConflict() throws Exception {
            when(productService.increaseStock(1L, -50L)).thenThrow(new DataIntegrityViolationException("재고가 음수일 수 없습니다."));

            mockMvc.perform(put("/api/product/increase_stock")
                            .param("productId", "1")
                            .param("stock", "-50"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.result.errorMessage").value("재고가 음수일 수 없습니다."));
        }
    }

    @Nested
    class DecreaseStockTest {
        @Test
        public void decreaseStock_ValidInput_ReturnsUpdatedStock() throws Exception {
            Product updatedProduct = new Product();
            updatedProduct.setId(1L);
            updatedProduct.setName("TestProduct");
            updatedProduct.setStock(50L);

            when(productService.decreaseStock(1L, 50L)).thenReturn(updatedProduct);

            mockMvc.perform(put("/api/product/decrease_stock")
                            .param("productId", "1")
                            .param("stock", "50"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.stock").value(50))
                    .andExpect(jsonPath("$.responseMessage").value("TestProduct상품이 50개 감소하여 50개가 되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        public void decreaseStock_InsufficientStock_ReturnsConflict() throws Exception {
            when(productService.decreaseStock(1L, 150L)).thenThrow(new DataIntegrityViolationException("재고가 부족합니다."));

            mockMvc.perform(put("/api/product/decrease_stock")
                            .param("productId", "1")
                            .param("stock", "150"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.result.errorMessage").value("재고가 부족합니다."));
        }
    }


}