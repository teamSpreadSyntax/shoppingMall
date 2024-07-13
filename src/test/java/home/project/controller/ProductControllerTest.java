package home.project.controller;

import home.project.SecurityConfig;
import home.project.domain.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@Import({SecurityConfig.class})
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
        pageable = PageRequest.of(1, 5);
        productPage = new PageImpl<>(products, pageable, products.size());
    }

    @Nested
    class CreateProductTest {
        @Test
        public void createProduct_Success_ReturnsCreatedProduct() throws Exception {
            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            doNothing().when(productService).join(any(Product.class));

            mockMvc.perform(post("/api/product/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"brand\": \"TestBrand\", \"category\": \"TestCategory\", \"name\": \"TestProduct\", \"stock\": 100, \"soldQuantity\": 0, \"image\": \"test.jpg\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.successMessage").value(product.getName() + "(이)가 등록되었습니다"))
                    .andExpect(jsonPath("$.responseMessage").value("상품 등록 성공"))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class FindProductByIdTest {
        @Test
        public void findProductById_Existing_ReturnsProductInfo() throws Exception {
            long productId = 1L;

            when(productService.findById(productId)).thenReturn(Optional.of(product));

            mockMvc.perform(get("/api/product/product")
                            .param("productId", String.valueOf(productId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(product.getId()))
                    .andExpect(jsonPath("$.result.brand").value(product.getBrand()))
                    .andExpect(jsonPath("$.result.soldQuantity").value(product.getSoldQuantity()))
                    .andExpect(jsonPath("$.result.name").value(product.getName()))
                    .andExpect(jsonPath("$.result.category").value(product.getCategory()))
                    .andExpect(jsonPath("$.result.stock").value(product.getStock()))
                    .andExpect(jsonPath("$.result.image").value(product.getImage()))
                    .andExpect(jsonPath("$.responseMessage").value(product.getId() + "에 해당하는 상품 입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class FindAllProductsTest {
        @Test
        public void findAllProducts_ReturnsProductsPage() throws Exception {

            when(productService.findAll(any(Pageable.class))).thenReturn(productPage);

            mockMvc.perform(get("/api/product/products")
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(products.size()))
                    .andExpect(jsonPath("$.result.content[0].id").value(productPage.getContent().get(0).getId()))
                    .andExpect(jsonPath("$.result.content[0].brand").value(productPage.getContent().get(0).getBrand()))
                    .andExpect(jsonPath("$.result.content[0].soldQuantity").value(productPage.getContent().get(0).getSoldQuantity()))
                    .andExpect(jsonPath("$.result.content[0].name").value(productPage.getContent().get(0).getName()))
                    .andExpect(jsonPath("$.result.content[0].category").value(productPage.getContent().get(0).getCategory()))
                    .andExpect(jsonPath("$.result.content[0].stock").value(productPage.getContent().get(0).getStock()))
                    .andExpect(jsonPath("$.result.content[0].image").value(productPage.getContent().get(0).getImage()))
                    .andExpect(jsonPath("$.responseMessage").value("전체 상품입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
        @Test
        public void findAllProducts_RequestOverPage_ReturnsEmptyPage() throws Exception {

            when(productService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

            mockMvc.perform(get("/api/product/products")
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(0))
                    .andExpect(jsonPath("$.responseMessage").value("전체 상품입니다."))
                    .andExpect(jsonPath("$.status").value(200));

        }
    }

    @Nested
    class SearchProductsTest {
        @Test
        public void searchProducts_ReturnsMatchingProducts() throws Exception {
            String brand = "TestBrand";
            String category = "TestCategory";
            String name = "TestName";
            String content = "search content";

            when(productService.findProducts(anyString(), anyString(), anyString(), anyString(), any(Pageable.class))).thenReturn(productPage);

            mockMvc.perform(get("/api/product/search")
                            .param("brand", brand)
                            .param("category", category)
                            .param("productName", name)
                            .param("content", content)
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(products.size()))
                    .andExpect(jsonPath("$.result.content[0].id").value(productPage.getContent().get(0).getId()))
                    .andExpect(jsonPath("$.result.content[0].brand").value(productPage.getContent().get(0).getBrand()))
                    .andExpect(jsonPath("$.result.content[0].soldQuantity").value(productPage.getContent().get(0).getSoldQuantity()))
                    .andExpect(jsonPath("$.result.content[0].name").value(productPage.getContent().get(0).getName()))
                    .andExpect(jsonPath("$.result.content[0].category").value(productPage.getContent().get(0).getCategory()))
                    .andExpect(jsonPath("$.result.content[0].stock").value(productPage.getContent().get(0).getStock()))
                    .andExpect(jsonPath("$.result.content[0].image").value(productPage.getContent().get(0).getImage()))
                    .andExpect(jsonPath("$.responseMessage").value("검색 결과입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class UpdateProductTest {
        @Test
        public void updateProduct_Success_ReturnsUpdatedProductInfo() throws Exception {
            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(productService.update(any(Product.class))).thenReturn(Optional.of(product));

            mockMvc.perform(put("/api/product/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"brand\": \"UpdatedBrand\", \"category\": \"UpdatedCategory\", \"name\": \"UpdatedProduct\", \"stock\": 200, \"soldQuantity\": 50, \"image\": \"updated.jpg\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(product.getId()))
                    .andExpect(jsonPath("$.result.brand").value(product.getBrand()))
                    .andExpect(jsonPath("$.result.soldQuantity").value(product.getSoldQuantity()))
                    .andExpect(jsonPath("$.result.name").value(product.getName()))
                    .andExpect(jsonPath("$.result.category").value(product.getCategory()))
                    .andExpect(jsonPath("$.result.stock").value(product.getStock()))
                    .andExpect(jsonPath("$.result.image").value(product.getImage()))
                    .andExpect(jsonPath("$.responseMessage").value("상품 정보가 수정되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class DeleteProductTest {
        @Test
        public void deleteProduct_Existing_DeletesProductAndReturnsSuccessMessage() throws Exception {
            long productId = 1L;

            doNothing().when(productService).deleteById(productId);

            mockMvc.perform(delete("/api/product/delete")
                            .param("productId", String.valueOf(productId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.thanksMessage").value(productId + "(이)가 삭제되었습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("상품 삭제 성공"))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class IncreaseStockTest {
        @Test
        public void increaseStock_Success_ReturnsUpdatedStock() throws Exception {

            Long id = 1L;
            String name = "TestName";
            Long stock = 200L;
            Long addStock = 50l;

            Product updatedProduct = new Product();
            updatedProduct.setId(id);
            updatedProduct.setName(name);
            updatedProduct.setStock(stock);

            when(productService.increaseStock(id, addStock)).thenReturn(updatedProduct);

            mockMvc.perform(put("/api/product/increase_stock")
                            .param("productId", String.valueOf(id))
                            .param("stock", String.valueOf(addStock)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.stock").value(200))
                    .andExpect(jsonPath("$.responseMessage").value(name + "상품이 " + addStock + "개 증가하여 " + stock + "개가 되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class DecreaseStockTest {
        @Test
        public void decreaseStock_Success_ReturnsUpdatedStock() throws Exception {

            Long id = 1L;
            String name = "TestName";
            Long stock = 200L;
            Long addStock = 50l;

            Product updatedProduct = new Product();
            updatedProduct.setId(id);
            updatedProduct.setName(name);
            updatedProduct.setStock(stock);

            when(productService.decreaseStock(id, addStock)).thenReturn(updatedProduct);

            mockMvc.perform(put("/api/product/decrease_stock")
                            .param("productId", String.valueOf(id))
                            .param("stock", String.valueOf(addStock)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.stock").value(200))
                    .andExpect(jsonPath("$.responseMessage").value(name + "상품이 " + addStock + "개 감소하여 " + stock + "개가 되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }
}