package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;
import home.project.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductServiceImplTest {
    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    private Product product;
    private Product product2;
    private Product product3;

    @BeforeEach
    public void setUp() {
        product = new Product();
        product.setId(1L);
        product.setBrand("nike");
        product.setName("에어맥스");
        product.setCategory("신발");
        product.setStock(0L);
        product.setImage("nike.jpg");

        product2 = new Product();
        product2.setId(2L);
        product2.setBrand("adidas");
        product2.setName("트레이닝");
        product2.setCategory("바지");
        product2.setStock(0L);
        product2.setImage("adidas.jpg");

        product3 = new Product();
        product3.setId(3L);
        product3.setBrand("puma");
        product3.setName("트레이닝 트랙");
        product3.setCategory("바지");
        product3.setStock(4L);
        product3.setImage("puma.jpg");

    }

    @Nested
    class JoinTests {
        @Test
        void join_ProductSuccessfully_SavesProductToRepositoryAndVerifiesBrand() {
            productService.join(product3);
            verify(productRepository).save(product3);
            assertEquals("puma", product3.getBrand());
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void findById_ExistingId_ReturnsProduct() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            Optional<Product> findProduct = productService.findById(1L);

            assertTrue(findProduct.isPresent());
            assertEquals(product, findProduct.get());
        }

        @Test
        void findById_NonExistingId_ThrowsException() {
            when(productRepository.findById(3L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.findById(3L));
            assertEquals("3(으)로 등록된 상품이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    class FindAllTests  {
        @Test
        void findAll_AllProductsFound_ReturnsPageOfProducts() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Product> productList = Arrays.asList(product, product2);
            Page<Product> page = new PageImpl<>(productList, pageable, productList.size());

            when(productRepository.findAll(pageable)).thenReturn(page);
            Page<Product> result = productService.findAll(pageable);

            assertEquals(2, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertEquals(productList, result.getContent());
        }
    }

    @Nested
    class FindProductsTests {
        @Test
        void findProducts_ByCategory_ReturnsMatchingProducts() {
            Pageable pageable = PageRequest.of(0, 10);

            List<Product> pantsProducts = Arrays.asList(product2);
            Page<Product> pantsProductsPage = new PageImpl<>(pantsProducts, pageable, pantsProducts.size());

            when(productRepository.findProducts(null, "바", null, null, pageable)).thenReturn(pantsProductsPage);

            Page<Product> result = productService.findProducts(null, "바", null, null, pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals("바지", result.getContent().get(0).getCategory());
            assertEquals(product2, result.getContent().get(0));
        }

        @Test
        void findProducts_NoMatchingProducts_ThrowsException() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> emptyPage = Page.empty(pageable);

            when(productRepository.findProducts("1", "2", "3", null, pageable)).thenReturn(emptyPage);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.findProducts("1", "2", "3", null, pageable));
            assertEquals("해당하는 상품이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    class BrandListTests {
        @Test
        void brandList_AllBrandsSorted_ReturnsPageOfProducts() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Product> products = Arrays.asList(product, product2);
            Page<Product> page = new PageImpl<>(products, pageable, products.size());
            when(productRepository.findAllByOrderByBrandAsc(pageable)).thenReturn(page);

            Page<Product> result = productService.brandList(pageable);
            assertEquals(2, result.getTotalElements());
            assertEquals(products, result.getContent());
        }
    }

    @Nested
    class UpdateTests {
        @Test
        void update_ProductSuccessfullyUpdated_ReturnsUpdatedProduct() {
            Product updateProduct = new Product();
            updateProduct.setId(1L);
            updateProduct.setBrand("puma");
            updateProduct.setStock(15L);
            updateProduct.setName("트레이닝");
            updateProduct.setSoldQuantity(5L);
            updateProduct.setCategory("바지");
            updateProduct.setImage("adidas.jpg");

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Optional<Product> result = productService.update(updateProduct);

            assertTrue(result.isPresent());
            Product updatedProduct = result.get();

            assertEquals("puma", updatedProduct.getBrand());
            assertEquals(15L, updatedProduct.getStock());
            assertEquals("트레이닝", updatedProduct.getName());
            assertEquals("바지", updatedProduct.getCategory());
            assertEquals("adidas.jpg", updatedProduct.getImage());
            assertEquals(5L, updatedProduct.getSoldQuantity());
        }
        @Test
        void update_ProductPartialChangeUpdated_ReturnUpdatedProduct(){
                Product updateProduct = new Product();
                updateProduct.setId(1L);
                updateProduct.setName("새로운 에어맥스");
                updateProduct.setStock(10L);

                when(productRepository.findById(1L)).thenReturn(Optional.of(product));
                when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

                Optional<Product> result = productService.update(updateProduct);

                assertTrue(result.isPresent());
                Product updatedProduct = result.get();
                assertEquals("새로운 에어맥스", updatedProduct.getName());
                assertEquals(10L, updatedProduct.getStock());
                assertEquals("nike", updatedProduct.getBrand());
                assertEquals("신발", updatedProduct.getCategory());
                assertEquals("nike.jpg", updatedProduct.getImage());

        }
        @Test
        void 업데이트_변경사항_없음(){
            Product updateProduct = new Product();
            updateProduct.setId(1L);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> productService.update(updateProduct));
            assertEquals("변경된 상품 정보가 없습니다.", exception.getMessage());
        }
        @Test
        void  업데이트_존재하지않는_제품(){
            Product updateProduct = new Product();
            updateProduct.setId(999L);

            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.findById(999L));
            assertEquals("999(으)로 등록된 상품이 없습니다.", exception.getMessage());
        }
        @Test
        void update_InvalidStockUpdate_ThrowsDataIntegrityViolationException() {

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            Product updatedProduct = new Product();
            updatedProduct.setId(1L);
            updatedProduct.setBrand("puma");
            updatedProduct.setStock(-15L);
            updatedProduct.setName("트레이닝");
            updatedProduct.setCategory("바지");
            updatedProduct.setImage("adidas.jpg");

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> productService.update(updatedProduct));
            assertEquals("재고가 음수일 수 없습니다.", exception.getMessage());
        }
    }

    @Nested
    class DeleteByIdTests {
        @Test
        void deleteById_ExistingId_ProductDeletedSuccessfully() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.deleteById(1L);

            verify(productRepository).deleteById(1L);
        }

        @Test
        void deleteById_NonExistingId_ThrowsException() {
            when(productRepository.findById(234L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.deleteById(234L));
            assertEquals("234(으)로 등록된 상품이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    class IncreaseStockTests {
        @Test
        void increaseStock_StockIncreasedSuccessfully_ReturnsUpdatedProduct() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);

            product.setStock(2L);

            Product updatedStock = productService.increaseStock(1L, 15L);

            verify(productRepository).save(product);
            assertEquals(17L, product.getStock());
            assertEquals(17L, updatedStock.getStock());
        }

        @Test
        void increaseStock_NegativeStockIncrement_ThrowsDataIntegrityViolationException() {

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> productService.increaseStock(1L, -5L));
            assertEquals("재고가 음수일 수 없습니다.", exception.getMessage());
        }

        @Test
        void increaseStock_NonExistingId_ThrowsIllegalArgumentException() {
            when(productRepository.findById(123L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.increaseStock(123L, 14L));
            assertEquals("123(으)로 등록된 상품이 없습니다.", exception.getMessage());

        }
    }
    @Nested
    class DecreaseStockTests {
        @Test
        void decreaseStock_StockDecreasedSuccessfully_ReturnsUpdatedProduct() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);

            product.setStock(20L);

            Product updatedStock = productService.decreaseStock(1L, 15L);

            verify(productRepository).save(product);
            assertEquals(5L, product.getStock());
            assertEquals(5L, updatedStock.getStock());
        }

        @Test
        void decreaseStock_InsufficientStock_ThrowsDataIntegrityViolationException() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            product.setStock(0L);
            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> productService.decreaseStock(1L, 5L));
            assertEquals("재고가 부족합니다.", exception.getMessage());
        }

        @Test
        void decreaseStock_NonExistingId_ThrowsIllegalArgumentException() {
            when(productRepository.findById(123L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.decreaseStock(123L, 14L));
            assertEquals("123(으)로 등록된 상품이 없습니다.", exception.getMessage());
        }
    }
}


