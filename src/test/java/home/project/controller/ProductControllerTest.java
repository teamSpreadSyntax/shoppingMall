package home.project.controller;

import home.project.controller.ProductController;
import home.project.domain.*;
import home.project.service.ProductService;
import home.project.service.ValidationCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    private ProductController productController;

    @Mock
    private ProductService productService;

    @Mock
    private ValidationCheck validationCheck;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        productController = new ProductController(productService, validationCheck);
    }

    @Test
    public void testCreateProduct_Success() {
        ProductDTOWithoutId productDTOWithoutId = new ProductDTOWithoutId();
        productDTOWithoutId.setBrand("Brand");
        productDTOWithoutId.setCategory("Category");
        productDTOWithoutId.setSelledcount(10L);
        productDTOWithoutId.setName("Product");
        productDTOWithoutId.setStock(100L);
        productDTOWithoutId.setImage("image.jpg");

        when(validationCheck.validationChecks(bindingResult)).thenReturn(null);

        Product product = new Product();
        product.setId(1L);
        product.setBrand(productDTOWithoutId.getBrand());
        product.setCategory(productDTOWithoutId.getCategory());
        product.setSelledcount(productDTOWithoutId.getSelledcount());
        product.setName(productDTOWithoutId.getName());
        product.setStock(productDTOWithoutId.getStock());
        product.setImage(productDTOWithoutId.getImage());

        doNothing().when(productService).join(any(Product.class));

        CustomOptionalResponseEntity<?> responseEntity = productController.createProduct(productDTOWithoutId, bindingResult);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testFindProductById_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");

        when(productService.findById(1L)).thenReturn(Optional.of(product));

        CustomOptionalResponseEntity<Optional<Product>> responseEntity = productController.findProductById(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("1에 해당하는 상품 입니다", responseEntity.getBody().ResponseMessage);
        assertEquals(Optional.of(product), responseEntity.getBody().result.get());
    }

    @Test
    public void testFindAllProduct_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");

        List<Product> products = Collections.singletonList(product);
        Page<Product> productPage = new PageImpl<>(products);

        when(productService.findAll(any(Pageable.class))).thenReturn(productPage);

        CustomListResponseEntity<Product> responseEntity = productController.findAllProduct(PageRequest.of(1, 5));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("전체 상품입니다", responseEntity.getBody().responseMessage);
        assertEquals(1 , productPage.getTotalElements());
    }

    @Test
    public void testSearchProducts_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");

        List<Product> products = Collections.singletonList(product);
        Page<Product> productPage = new PageImpl<>(products);

        when(productService.findProducts(anyString(), anyString(), anyString(), anyString(), any(Pageable.class))).thenReturn(productPage);

        ResponseEntity<?> responseEntity = productController.searchProducts("Brand", "Category", "Product", "Content", PageRequest.of(0, 5));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteProduct_Success() {
        doNothing().when(productService).deleteById(1L);

        CustomOptionalResponseEntity<Optional<Product>> responseEntity = productController.deleteProduct(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testIncreaseStock_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setStock(150L);

        when(productService.increaseStock(1L, 50L)).thenReturn(product);

        CustomOptionalResponseEntity<Product> responseEntity = productController.increaseStock(1L, 50L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Product 상품이50개 증가하여150개가 되었습니다", responseEntity.getBody().ResponseMessage);
        assertEquals(product, responseEntity.getBody().result.get());
    }

    @Test
    public void testDecreaseStock_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setStock(50L);

        when(productService.decreaseStock(1L, 50L)).thenReturn();

        CustomOptionalResponseEntity<Product> responseEntity = productController.decreaseStock(1L, 50L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Product상품이50개 감소하여0개가 되었습니다", responseEntity.getBody().ResponseMessage);
        assertEquals(product, responseEntity.getBody().result.get());
    }

    @Test
    public void testUpdateProduct_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setBrand("UpdatedBrand");
        product.setCategory("UpdatedCategory");
        product.setName("UpdatedProduct");
        product.setStock(100L);
        product.setImage("updated_image.jpg");

        when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
        when(productService.update(any(Product.class))).thenReturn(Optional.of(product));

        CustomOptionalResponseEntity<?> responseEntity = productController.updateProduct(product, bindingResult);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("상품정보가 수정되었습니다", responseEntity.getBody().ResponseMessage);
        assertEquals(product, responseEntity.getBody().result.get());
    }
}
