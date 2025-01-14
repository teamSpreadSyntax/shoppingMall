//package home.project.controller;
//
//import home.project.config.security.SecurityConfig;
//import home.project.domain.*;
//import home.project.dto.ProductDTOWithoutId;
//import home.project.exceptions.GlobalExceptionHandler;
//import home.project.exceptions.exception.IdNotFoundException;
//import home.project.response.CustomOptionalResponseBody;
//import home.project.response.CustomOptionalResponseEntity;
//import home.project.service.security.JwtTokenProvider;
//import home.project.service.product.ProductService;
//import home.project.service.validation.ValidationCheck;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.validation.BindingResult;
//
//import java.util.*;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ProductController.class)
//@Import({SecurityConfig.class, GlobalExceptionHandler.class})
//@ExtendWith(MockitoExtension.class)
//public class ProductControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private JwtTokenProvider jwtTokenProvider;
//
//    @MockBean
//    private UserDetailsService userDetailsService;
//
//    @MockBean
//    private ProductService productService;
//
//    @MockBean
//    private ValidationCheck validationCheck;
//
//    @Mock
//    private BindingResult bindingResult;
//
//    private ProductDTOWithoutId productDTOWithoutId;
//    private Product product;
//    private List<Product> productList;
//    private Page<Product> productPage;
//    private Pageable pageable;
//
//    @BeforeEach
//    public void setUp() {
//        productDTOWithoutId = new ProductDTOWithoutId();
//        productDTOWithoutId.setBrand("TestBrand");
//        productDTOWithoutId.setCategory("TestCategory");
//        productDTOWithoutId.setName("TestProduct");
//        productDTOWithoutId.setStock(100L);
//        productDTOWithoutId.setSoldQuantity(10L);
//
//        product = new Product();
//        product.setId(1L);
//        product.setBrand(productDTOWithoutId.getBrand());
//        product.setCategory(productDTOWithoutId.getCategory());
//        product.setName(productDTOWithoutId.getName());
//        product.setStock(productDTOWithoutId.getStock());
//        product.setSoldQuantity(productDTOWithoutId.getSoldQuantity());
//
//        Product product2 = new Product();
//        product2.setId(2L);
//        product2.setBrand("AnotherBrand");
//        product2.setCategory("AnotherCategory");
//        product2.setName("AnotherProduct");
//        product2.setStock(100L);
//        product2.setSoldQuantity(10L);
//
//        productList = Arrays.asList(product, product2);
//        pageable = PageRequest.of(1, 5);
//        productPage = new PageImpl<>(productList, pageable, productList.size());
//    }
//
//    @Nested
//    @WithMockUser(roles = {"ADMIN", "CENTER"})
//    class CreateProductTests {
//        @Test
//        public void createProduct_ValidInput_ReturnsCreatedProduct() throws Exception {
//            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(null);
//            doNothing().when(productService).join(any(Product.class));
//
//            mockMvc.perform(post("/api/product/create")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{ \"brand\": \"TestBrand\", \"category\": \"TestCategory\", \"name\": \"TestProduct\", \"stock\": 100, \"soldQuantity\": 0, \"image\": \"test.jpg\" }"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result.successMessage").value(product.getName() + "(이)가 등록되었습니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("상품 등록 성공"))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//
//        @Test
//        public void createProduct_EmptyInput_ReturnsBadRequest() throws Exception {
//            Map<String, String> errors = new HashMap<>();
//            errors.put("name", "상품의 이름을 입력해주세요.");
//            errors.put("brand", "상품의 브랜드를 입력해주세요.");
//            errors.put("category", "상품의 카테고리를 입력해주세요.");
//            errors.put("image", "상품의 이미지를 입력해주세요.");
//            when(validationCheck.validationChecks(any(BindingResult.class)))
//                    .thenReturn(new CustomOptionalResponseEntity<>(
//                            new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value()),
//                            HttpStatus.BAD_REQUEST
//                    ));
//
//            mockMvc.perform(post("/api/product/create")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{ \"brand\": \"\", \"category\": \"\", \"name\": \"\", \"stock\": 100, \"soldQuantity\": 0, \"image\": \"\" }"))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.result").exists())
//                    .andExpect(jsonPath("$.result.name").value("상품의 이름을 입력해주세요."))
//                    .andExpect(jsonPath("$.result.brand").value("상품의 브랜드를 입력해주세요."))
//                    .andExpect(jsonPath("$.result.category").value("상품의 카테고리를 입력해주세요."))
//                    .andExpect(jsonPath("$.result.image").value("상품의 이미지를 입력해주세요."))
//                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
//                    .andExpect(jsonPath("$.status").value(400));
//        }
//
//        @Test
//        public void createProduct_NegativeStock_ReturnsConflict() throws Exception {
//            doThrow(new IllegalStateException("재고가 음수일 수 없습니다.")).when(productService).join(any(Product.class));
//
//            mockMvc.perform(post("/api/product/create")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{ \"brand\": \"TestBrand\", \"category\": \"TestCategory\", \"name\": \"TestProduct\", \"stock\": -50, \"soldQuantity\": 0, \"image\": \"test.jpg\" }"))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.result.errorMessage").value("재고가 음수일 수 없습니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
//                    .andExpect(jsonPath("$.status").value(400));
//        }
//
//    }
//
//    @Nested
//    @WithMockUser(roles = {"USER", "ADMIN", "CENTER"})
//    class FindProductByIdTests {
//        @Test
//        public void findProductById_ExistingProduct_ReturnsProductInfo() throws Exception {
//            when(productService.findById(1L)).thenReturn(Optional.of(product));
//
//            mockMvc.perform(get("/api/product/product")
//                            .param("productId", "1"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result").exists())
//                    .andExpect(jsonPath("$.result.id").value(product.getId()))
//                    .andExpect(jsonPath("$.result.name").value(product.getName()))
//                    .andExpect(jsonPath("$.result.brand").value(product.getBrand()))
//                    .andExpect(jsonPath("$.result.category").value(product.getCategory()))
//                    .andExpect(jsonPath("$.result.stock").value(product.getStock()))
//                    .andExpect(jsonPath("$.result.soldQuantity").value(product.getSoldQuantity()))
//                    .andExpect(jsonPath("$.responseMessage").value("1에 해당하는 상품 입니다."))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//
//        @Test
//        public void findProductById_NonExistingProduct_ReturnsNotFound() throws Exception {
//            when(productService.findById(99L)).thenThrow(new IdNotFoundException("99(으)로 등록된 상품이 없습니다."));
//
//            mockMvc.perform(get("/api/product/product")
//                            .param("productId", "99"))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.result.errorMessage").value("99(으)로 등록된 상품이 없습니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
//                    .andExpect(jsonPath("$.status").value(404));
//        }
//    }
//
//    @Nested
//    @WithMockUser(roles = {"USER", "ADMIN", "CENTER"})
//    class FindAllProductsTests {
//        @Test
//        public void findAllProducts_ExistingProducts_ReturnsProductsPage() throws Exception {
//            when(productService.findAll(any(Pageable.class))).thenReturn(productPage);
//
//            mockMvc.perform(get("/api/product/products")
//                            .param("page", "1")
//                            .param("size", "5"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result").exists())
//                    .andExpect(jsonPath("$.result.totalCount").value(productPage.getTotalElements()))
//                    .andExpect(jsonPath("$.result.page").value(productPage.getNumber()))
//                    .andExpect(jsonPath("$.result.content").isArray())
//                    .andExpect(jsonPath("$.result.content.length()").value(productList.size()))
//                    .andExpect(jsonPath("$.result.content[0].id").value(productPage.getContent().get(0).getId()))
//                    .andExpect(jsonPath("$.result.content[0].name").value(productPage.getContent().get(0).getName()))
//                    .andExpect(jsonPath("$.result.content[0].brand").value(productPage.getContent().get(0).getBrand()))
//                    .andExpect(jsonPath("$.result.content[0].category").value(productPage.getContent().get(0).getCategory()))
//                    .andExpect(jsonPath("$.result.content[0].stock").value(productPage.getContent().get(0).getStock()))
//                    .andExpect(jsonPath("$.result.content[0].soldQuantity").value(productPage.getContent().get(0).getSoldQuantity()))
//                    .andExpect(jsonPath("$.result.content[1].id").value(productPage.getContent().get(1).getId()))
//                    .andExpect(jsonPath("$.result.content[1].name").value(productPage.getContent().get(1).getName()))
//                    .andExpect(jsonPath("$.result.content[1].brand").value(productPage.getContent().get(1).getBrand()))
//                    .andExpect(jsonPath("$.result.content[1].category").value(productPage.getContent().get(1).getCategory()))
//                    .andExpect(jsonPath("$.result.content[1].stock").value(productPage.getContent().get(1).getStock()))
//                    .andExpect(jsonPath("$.result.content[1].soldQuantity").value(productPage.getContent().get(1).getSoldQuantity()))
//                    .andExpect(jsonPath("$.responseMessage").value("전체 상품입니다."))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//
//        @Test
//        public void findAllProducts_EmptyPage_ReturnsEmptyPage() throws Exception {
//            when(productService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
//
//            mockMvc.perform(get("/api/product/products")
//                            .param("page", "1000")
//                            .param("size", "5"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result.totalCount").value(0))
//                    .andExpect(jsonPath("$.result.page").value(0))
//                    .andExpect(jsonPath("$.result.content").isArray())
//                    .andExpect(jsonPath("$.result.content.length()").value(0))
//                    .andExpect(jsonPath("$.responseMessage").value("전체 상품입니다."))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//
//        @Test
//        public void findAllProducts_NegativePageNumber_ReturnsBadRequest() throws Exception {
//            when(productService.findAll(any(Pageable.class))).thenThrow(new IllegalAccessError("Page index must not be less than zero"));
//
//            mockMvc.perform(get("/api/product/products")
//                            .param("page", "-1")
//                            .param("size", "5"))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.result.errorMessage").value("Page index must not be less than zero"))
//                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
//                    .andExpect(jsonPath("$.status").value(400));
//        }
//
//    }
//
//    @Nested
//    @WithMockUser(roles = {"USER", "ADMIN", "CENTER"})
//    class SearchProductsTests {
//        @Test
//        public void searchProducts_ExistingProducts_ReturnsMatchingProductsPage() throws Exception {
//            when(productService.findProducts(anyString(), anyString(), anyString(), anyString(), any(Pageable.class))).thenReturn(productPage);
//
//            mockMvc.perform(get("/api/product/search")
//                            .param("brand", "TestBrand")
//                            .param("category", "TestCategory")
//                            .param("productName", "TestProduct")
//                            .param("content", "TestContent")
//                            .param("page", "1")
//                            .param("size", "5"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result").exists())
//                    .andExpect(jsonPath("$.result.totalCount").value(productPage.getTotalElements()))
//                    .andExpect(jsonPath("$.result.page").value(productPage.getNumber()))
//                    .andExpect(jsonPath("$.result.content").isArray())
//                    .andExpect(jsonPath("$.result.content.length()").value(productList.size()))
//                    .andExpect(jsonPath("$.result.content[0].id").value(productList.get(0).getId()))
//                    .andExpect(jsonPath("$.result.content[0].name").value(productList.get(0).getName()))
//                    .andExpect(jsonPath("$.result.content[0].brand").value(productList.get(0).getBrand()))
//                    .andExpect(jsonPath("$.result.content[0].category").value(productList.get(0).getCategory()))
//                    .andExpect(jsonPath("$.result.content[0].stock").value(productList.get(0).getStock()))
//                    .andExpect(jsonPath("$.result.content[0].soldQuantity").value(productList.get(0).getSoldQuantity()))
//                    .andExpect(jsonPath("$.result.content[1].id").value(productList.get(1).getId()))
//                    .andExpect(jsonPath("$.result.content[1].name").value(productList.get(1).getName()))
//                    .andExpect(jsonPath("$.result.content[1].brand").value(productList.get(1).getBrand()))
//                    .andExpect(jsonPath("$.result.content[1].category").value(productList.get(1).getCategory()))
//                    .andExpect(jsonPath("$.result.content[1].stock").value(productList.get(1).getStock()))
//                    .andExpect(jsonPath("$.result.content[1].soldQuantity").value(productList.get(1).getSoldQuantity()))
//                    .andExpect(jsonPath("$.responseMessage").value("검색 키워드 : TestBrand, TestCategory, TestProduct, TestContent"))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//
//        @Test
//        public void searchProducts_NoKeywords_ReturnsAllProducts() throws Exception {
//            when(productService.findProducts(any(), any(), any(), any(), any(Pageable.class))).thenReturn(productPage);
//
//
//            mockMvc.perform(get("/api/product/search")
//                            .param("page", String.valueOf(pageable.getPageNumber()))
//                            .param("size", String.valueOf(pageable.getPageSize())))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result").exists())
//                    .andExpect(jsonPath("$.result.totalCount").value(productPage.getTotalElements()))
//                    .andExpect(jsonPath("$.result.page").value(productPage.getNumber()))
//                    .andExpect(jsonPath("$.result.content").isArray())
//                    .andExpect(jsonPath("$.result.content.length()").value(productList.size()))
//                    .andExpect(jsonPath("$.result.content[0].id").value(productPage.getContent().get(0).getId()))
//                    .andExpect(jsonPath("$.result.content[0].name").value(productPage.getContent().get(0).getName()))
//                    .andExpect(jsonPath("$.result.content[0].brand").value(productPage.getContent().get(0).getBrand()))
//                    .andExpect(jsonPath("$.result.content[0].category").value(productPage.getContent().get(0).getCategory()))
//                    .andExpect(jsonPath("$.result.content[0].stock").value(productPage.getContent().get(0).getStock()))
//                    .andExpect(jsonPath("$.result.content[0].soldQuantity").value(productPage.getContent().get(0).getSoldQuantity()))
//                    .andExpect(jsonPath("$.result.content[1].id").value(productPage.getContent().get(1).getId()))
//                    .andExpect(jsonPath("$.result.content[1].name").value(productPage.getContent().get(1).getName()))
//                    .andExpect(jsonPath("$.result.content[1].brand").value(productPage.getContent().get(1).getBrand()))
//                    .andExpect(jsonPath("$.result.content[1].category").value(productPage.getContent().get(1).getCategory()))
//                    .andExpect(jsonPath("$.result.content[1].stock").value(productPage.getContent().get(1).getStock()))
//                    .andExpect(jsonPath("$.result.content[1].soldQuantity").value(productPage.getContent().get(1).getSoldQuantity()))
//                    .andExpect(jsonPath("$.responseMessage").value("전체 상품입니다."))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//
//        @Test
//        public void searchProducts_EmptyPage_ReturnsEmptyPage() throws Exception {
//            when(productService.findProducts(any(), any(), any(), any(), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
//
//            mockMvc.perform(get("/api/product/search")
//                            .param("page", "1000")  // 존재하지 않는 페이지 번호
//                            .param("size", "5"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result.totalCount").value(0))
//                    .andExpect(jsonPath("$.result.page").value(0))
//                    .andExpect(jsonPath("$.result.content").isArray())
//                    .andExpect(jsonPath("$.result.content.length()").value(0))
//                    .andExpect(jsonPath("$.responseMessage").value("전체 상품입니다."))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//        @Test
//        public void searchProducts_NoResults_ReturnsNotFound() throws Exception {
//            when(productService.findProducts(anyString(), anyString(), anyString(), anyString(), any(Pageable.class)))
//                    .thenThrow(new IdNotFoundException("해당하는 상품이 없습니다."));
//
//            mockMvc.perform(get("/api/product/search")
//                    .param("brand", "TestBrand")
//                    .param("category", "TestCategory")
//                    .param("productName", "TestProduct")
//                    .param("content", "TestContent")
//                    .param("page", "1")
//                    .param("size", "5"))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.result.errorMessage").value("해당하는 상품이 없습니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
//                    .andExpect(jsonPath("$.status").value(404));
//        }
//        @Test
//        public void searchProducts_NegativePageNumber_ReturnsBadRequest() throws Exception {
//            when(productService.findProducts(anyString(), anyString(), anyString(), anyString(), any(Pageable.class))).thenThrow(new IllegalAccessError("Page index must not be less than zero"));
//
//            mockMvc.perform(get("/api/product/search")
//                            .param("brand", "TestBrand")
//                            .param("category", "TestCategory")
//                            .param("productName", "TestProduct")
//                            .param("content", "TestContent")
//                            .param("page", "-1")
//                            .param("size", "5"))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.result.errorMessage").value("Page index must not be less than zero"))
//                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
//                    .andExpect(jsonPath("$.status").value(400));
//        }
//    }
//
//    @Nested
//    @WithMockUser(roles = {"USER", "ADMIN", "CENTER"})
//    class BrandListTests {
//        @Test
//        void brandList_ExistingProducts_ReturnsProductsPage() throws Exception {
//            Page<Product> brandPage = new PageImpl<>(productList);
//            when(productService.brandList(any(Pageable.class))).thenReturn(brandPage);
//
//            mockMvc.perform(get("/api/product/brands")
//                            .param("page", "1")
//                            .param("size", "5"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result.totalCount").value(brandPage.getTotalElements()))
//                    .andExpect(jsonPath("$.result.page").value(brandPage.getNumber()))
//                    .andExpect(jsonPath("$.result.content").isArray())
//                    .andExpect(jsonPath("$.result.content.length()").value(productList.size()))
//                    .andExpect(jsonPath("$.result.content[0].brand").value("TestBrand"))
//                    .andExpect(jsonPath("$.result.content[1].brand").value("AnotherBrand"))
//                    .andExpect(jsonPath("$.responseMessage").value("전체 브랜드 입니다."))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//        @Test
//        public void brandList_EmptyPage_ReturnsEmptyPage() throws Exception {
//            when(productService.brandList(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
//
//            mockMvc.perform(get("/api/product/brands")
//                            .param("page", "1000")
//                            .param("size", "5"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result.totalCount").value(0))
//                    .andExpect(jsonPath("$.result.page").value(0))
//                    .andExpect(jsonPath("$.result.content").isArray())
//                    .andExpect(jsonPath("$.result.content.length()").value(0))
//                    .andExpect(jsonPath("$.responseMessage").value("전체 브랜드 입니다."))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//
//        @Test
//        public void brandList_NegativePageNumber_ReturnsBadRequest() throws Exception {
//            when(productService.brandList(any(Pageable.class))).thenThrow(new IllegalAccessError("Page index must not be less than zero"));
//
//            mockMvc.perform(get("/api/product/brands")
//                            .param("page", "-1")
//                            .param("size", "5"))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.result.errorMessage").value("Page index must not be less than zero"))
//                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
//                    .andExpect(jsonPath("$.status").value(400));
//        }
//    }
//
//    @Nested
//    @WithMockUser(roles = {"USER", "ADMIN", "CENTER"})
//    class UpdateProductTests {
//        @Test
//        public void updateProduct_ExistingProduct_ReturnsUpdatedProduct() throws Exception {
//            Product updatedProduct = new Product();
//            updatedProduct.setId(1L);
//            updatedProduct.setBrand("UpdatedBrand");
//            updatedProduct.setCategory("UpdatedCategory");
//            updatedProduct.setName("UpdatedProduct");
//            updatedProduct.setStock(200L);
//            updatedProduct.setSoldQuantity(50L);
//
//            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(null);
//            when(productService.update(any(Product.class))).thenReturn(Optional.of(updatedProduct));
//
//            mockMvc.perform(put("/api/product/update")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{ \"id\": 1, \"brand\": \"UpdatedBrand\", \"category\": \"UpdatedCategory\", \"name\": \"UpdatedProduct\", \"stock\": 200, \"soldQuantity\": 50, \"image\": \"updated.jpg\" }"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result").exists())
//                    .andExpect(jsonPath("$.result.id").value(updatedProduct.getId()))
//                    .andExpect(jsonPath("$.result.name").value(updatedProduct.getName()))
//                    .andExpect(jsonPath("$.result.brand").value(updatedProduct.getBrand()))
//                    .andExpect(jsonPath("$.result.category").value(updatedProduct.getCategory()))
//                    .andExpect(jsonPath("$.result.stock").value(updatedProduct.getStock()))
//                    .andExpect(jsonPath("$.result.soldQuantity").value(updatedProduct.getSoldQuantity()))
//                    .andExpect(jsonPath("$.responseMessage").value("상품 정보가 수정되었습니다."))
//                    .andExpect(jsonPath("$.status").value(200));
//
//            verify(productService).update(any(Product.class));
//        }
//
//        @Test
//        public void updateProduct_EmptyInput_ReturnsBadRequest() throws Exception {
//            Map<String, String> errors = new HashMap<>();
//            errors.put("name", "상품의 이름을 입력해주세요.");
//            errors.put("brand", "상품의 브랜드를 입력해주세요.");
//            errors.put("category", "상품의 카테고리를 입력해주세요.");
//            errors.put("image", "상품의 이미지를 입력해주세요.");
//            when(validationCheck.validationChecks(any(BindingResult.class)))
//                    .thenReturn(new CustomOptionalResponseEntity<>(
//                            new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value()),
//                            HttpStatus.BAD_REQUEST
//                    ));
//
//            mockMvc.perform(put("/api/product/update")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{ \"id\": 1, \"brand\": \"\", \"category\": \"\", \"name\": \"\", \"stock\": 100, \"soldQuantity\": 50, \"image\": \"\" }"))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.result").exists())
//                    .andExpect(jsonPath("$.result.name").value("상품의 이름을 입력해주세요."))
//                    .andExpect(jsonPath("$.result.brand").value("상품의 브랜드를 입력해주세요."))
//                    .andExpect(jsonPath("$.result.category").value("상품의 카테고리를 입력해주세요."))
//                    .andExpect(jsonPath("$.result.image").value("상품의 이미지를 입력해주세요."))
//                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
//                    .andExpect(jsonPath("$.status").value(400));
//        }
//
//
//
//        @Test
//        public void updateProduct_NonExistingProduct_ReturnsNotFound() throws Exception {
//            Long nonExistingId = 99L;
//            ProductDTOWithoutId updatedProductDTO = new ProductDTOWithoutId();
//            updatedProductDTO.setBrand("UpdatedBrand");
//            updatedProductDTO.setCategory("UpdatedCategory");
//            updatedProductDTO.setName("UpdatedProduct");
//            updatedProductDTO.setStock(200L);
//            updatedProductDTO.setSoldQuantity(50L);
//
//            String requestBody = String.format(
//                    "{ \"id\": %d, \"brand\": \"%s\", \"category\": \"%s\", \"name\": \"%s\", \"stock\": %d, \"soldQuantity\": %d, \"image\": \"%s\" }",
//                    nonExistingId, updatedProductDTO.getBrand(), updatedProductDTO.getCategory(), updatedProductDTO.getName(),
//                    updatedProductDTO.getStock(), updatedProductDTO.getSoldQuantity()
//            );
//
//            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(null);
//            when(productService.update(any(Product.class))).thenThrow(new IdNotFoundException(nonExistingId + "(으)로 등록된 상품이 없습니다."));
//
//            mockMvc.perform(put("/api/product/update")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(requestBody))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.result.errorMessage").value(nonExistingId + "(으)로 등록된 상품이 없습니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
//                    .andExpect(jsonPath("$.status").value(404));
//
//            verify(productService).update(any(Product.class));
//        }
//
//        @Test
//        public void updateProduct_NoChanges_ReturnsConflict() throws Exception {
//            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(null);
//            when(productService.update(any(Product.class))).thenThrow(new DataIntegrityViolationException("변경된 상품 정보가 없습니다."));
//
//
//            mockMvc.perform(put("/api/product/update")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{ \"id\": 1, \"brand\": \"TestBrand\", \"category\": \"TestCategory\", \"name\": \"TestProduct\", \"stock\": 100, \"soldQuantity\": 10, \"image\": \"test.jpg\" }"))
//                    .andExpect(status().isConflict())
//                    .andExpect(jsonPath("$.result").exists())
//                    .andExpect(jsonPath("$.result.errorMessage").value("변경된 상품 정보가 없습니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
//                    .andExpect(jsonPath("$.status").value(409));
//
//            verify(productService).update(any(Product.class));
//        }
//
//    @Test
//    public void updateProduct_NegativeStock_ReturnsBadRequest() throws Exception {
//        Map<String, String> errors = new HashMap<>();
//        errors.put("stock", "재고는 0 이상이어야 합니다.");
//        when(validationCheck.validationChecks(any(BindingResult.class)))
//                .thenReturn(new CustomOptionalResponseEntity<>(
//                        new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value()),
//                        HttpStatus.BAD_REQUEST
//                ));
//
//        mockMvc.perform(put("/api/product/update")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"id\": 1, \"brand\": \"UpdatedBrand\", \"category\": \"UpdatedCategory\", \"name\": \"UpdatedProduct\", \"stock\": -1, \"soldQuantity\": 50, \"image\": \"updated.jpg\" }"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.result.stock").value("재고는 0 이상이어야 합니다."))
//                .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
//                .andExpect(jsonPath("$.status").value(400));
//
//        verify(productService, never()).update(any(Product.class));
//    }
//    }
//    @Nested
//    @WithMockUser(roles = {"ADMIN", "CENTER"})
//    class DeleteProductTests {
//        @Test
//        public void deleteProduct_ExistingProduct_ReturnsSuccessMessage() throws Exception {
//            when(productService.findById(1L)).thenReturn(Optional.of(product));
//            doNothing().when(productService).deleteById(1L);
//
//            mockMvc.perform(delete("/api/product/delete")
//                            .param("productId", "1"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result.successMessage").value(product.getName() + "(id:1)(이)가 삭제되었습니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("상품 삭제 성공"))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//
//        @Test
//        public void deleteProduct_NonExistingProduct_ReturnsNotFound() throws Exception {
//            when(productService.findById(99L)).thenThrow(new IdNotFoundException("99(으)로 등록된 상품이 없습니다."));
//
//            mockMvc.perform(delete("/api/product/delete")
//                            .param("productId", "99"))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.result.errorMessage").value("99(으)로 등록된 상품이 없습니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
//                    .andExpect(jsonPath("$.status").value(404));
//        }
//    }
//
//    @Nested
//    @WithMockUser(roles = {"ADMIN", "CENTER"})
//    class IncreaseStockTests {
//        @Test
//        public void increaseStock_ExistingProduct_ReturnsUpdatedStock() throws Exception {
//            Product updatedProduct = new Product();
//            updatedProduct.setId(1L);
//            updatedProduct.setName("TestProduct");
//            updatedProduct.setStock(150L);
//
//            when(productService.increaseStock(1L, 50L)).thenReturn(updatedProduct);
//
//            mockMvc.perform(put("/api/product/increase_stock")
//                            .param("productId", "1")
//                            .param("stock", "50"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result.stock").value(150))
//                    .andExpect(jsonPath("$.responseMessage").value("TestProduct상품이 50개 증가하여 150개가 되었습니다."))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//        @Test
//        void increaseStock_NonExistingProduct_ReturnsNotFound() throws Exception {
//            when(productService.increaseStock(99L, 50L)).thenThrow(new IdNotFoundException("99(으)로 등록된 상품이 없습니다."));
//
//            mockMvc.perform(put("/api/product/increase_stock")
//                            .param("productId", "99")
//                            .param("stock", "50"))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.result.errorMessage").value("99(으)로 등록된 상품이 없습니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
//                    .andExpect(jsonPath("$.status").value(404));
//        }
//        @Test
//        public void increaseStock_NegativeStock_ReturnsConflict() throws Exception {
//            when(productService.increaseStock(1L, -50L)).thenThrow(new DataIntegrityViolationException("재고가 음수일 수 없습니다."));
//
//            mockMvc.perform(put("/api/product/increase_stock")
//                            .param("productId", "1")
//                            .param("stock", "-50"))
//                    .andExpect(status().isConflict())
//                    .andExpect(jsonPath("$.result.errorMessage").value("재고가 음수일 수 없습니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
//                    .andExpect(jsonPath("$.status").value(409));
//        }
//    }
//
//    @Nested
//    @WithMockUser(roles = {"ADMIN", "CENTER"})
//    class DecreaseStockTests {
//        @Test
//        public void decreaseStock_ExistingProduct_ReturnsUpdatedStock() throws Exception {
//            Product updatedProduct = new Product();
//            updatedProduct.setId(1L);
//            updatedProduct.setName("TestProduct");
//            updatedProduct.setStock(50L);
//
//            when(productService.decreaseStock(1L, 50L)).thenReturn(updatedProduct);
//
//            mockMvc.perform(put("/api/product/decrease_stock")
//                            .param("productId", "1")
//                            .param("stock", "50"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.result.stock").value(50))
//                    .andExpect(jsonPath("$.responseMessage").value("TestProduct상품이 50개 감소하여 50개가 되었습니다."))
//                    .andExpect(jsonPath("$.status").value(200));
//        }
//
//        @Test
//        void decreaseStock_NonExistingProduct_ReturnsNotFound() throws Exception {
//            when(productService.decreaseStock(99L, 50L)).thenThrow(new IdNotFoundException("99(으)로 등록된 상품이 없습니다."));
//
//            mockMvc.perform(put("/api/product/decrease_stock")
//                            .param("productId", "99")
//                            .param("stock", "50"))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.result.errorMessage").value("99(으)로 등록된 상품이 없습니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
//                    .andExpect(jsonPath("$.status").value(404));
//        }
//        @Test
//        public void decreaseStock_InsufficientStock_ReturnsConflict() throws Exception {
//            when(productService.decreaseStock(1L, 150L)).thenThrow(new DataIntegrityViolationException("재고가 부족합니다."));
//
//            mockMvc.perform(put("/api/product/decrease_stock")
//                            .param("productId", "1")
//                            .param("stock", "150"))
//                    .andExpect(status().isConflict())
//                    .andExpect(jsonPath("$.result.errorMessage").value("재고가 부족합니다."))
//                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
//                    .andExpect(jsonPath("$.status").value(409));
//        }
//    }
//
//
//}