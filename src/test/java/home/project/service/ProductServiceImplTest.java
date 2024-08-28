package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;
import home.project.exceptions.IdNotFoundException;
import home.project.exceptions.NoChangeException;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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
    private Product updateProduct;
    private Pageable pageable;

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

        updateProduct = new Product();
        updateProduct.setId(product.getId());
        updateProduct.setBrand("puma");
        updateProduct.setStock(15L);
        updateProduct.setName("트레이닝");
        updateProduct.setSoldQuantity(5L);
        updateProduct.setCategory("바지");
        updateProduct.setImage("adidas.jpg");

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    class JoinTests {
        @Test
        void join_ValidInput_SavesProduct() {
            productService.join(product3);
            verify(productRepository).save(product3);
            assertEquals("puma", product3.getBrand());
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void findById_ExistingProduct_ReturnsProduct() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            Optional<Product> findProduct = productService.findById(1L);

            assertTrue(findProduct.isPresent());
            assertEquals(product, findProduct.get());
        }

        @Test
        void findById_NonExistingProduct_ThrowsIllegalArgumentException() {
            when(productRepository.findById(3L)).thenReturn(Optional.empty());

            IdNotFoundException exception = assertThrows(IdNotFoundException.class, () -> productService.findById(3L));
            assertEquals("3(으)로 등록된 상품이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    class FindAllTests  {
        @Test
        void findAll_ExistingProducts_ReturnsProductsPage() {
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
        void findProducts_ExistingProducts_ReturnsMatchingProducts() {
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
        void findProducts_NoMatchingProducts_ThrowsIllegalArgumentException() {
            Page<Product> emptyPage = Page.empty(pageable);

            when(productRepository.findProducts("1", "2", "3", null, pageable)).thenReturn(emptyPage);

            IdNotFoundException exception = assertThrows(IdNotFoundException.class, () -> productService.findProducts("1", "2", "3", null, pageable));
            assertEquals("해당하는 상품이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    class BrandListTests {
        @Test
        void brandList_ExistingProducts_ReturnsProductsPage() {
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
        void update_ExistingProduct_ReturnsUpdatedProduct() {
            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Optional<Product> result = productService.update(updateProduct);

            assertTrue(result.isPresent());
            Product updatedProduct = result.get();

            assertEquals(updateProduct.getBrand(), updatedProduct.getBrand());
            assertEquals(updateProduct.getStock(), updatedProduct.getStock());
            assertEquals(updateProduct.getName(), updatedProduct.getName());
            assertEquals(updateProduct.getCategory(), updatedProduct.getCategory());
            assertEquals(updateProduct.getImage(), updatedProduct.getImage());
            assertEquals(updateProduct.getSoldQuantity(), updatedProduct.getSoldQuantity());
        }

        @Test
        void update_PartialChange_ReturnsUpdatedProduct(){
            Product partialUpdateProduct = new Product();
            partialUpdateProduct.setId(product.getId());
            partialUpdateProduct.setName(updateProduct.getName());
            partialUpdateProduct.setStock(updateProduct.getStock());

            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Optional<Product> result = productService.update(partialUpdateProduct);

            assertTrue(result.isPresent());
            Product updatedProduct = result.get();
            assertEquals(updateProduct.getName(), updatedProduct.getName());
            assertEquals(updateProduct.getStock(), updatedProduct.getStock());
            assertEquals(product.getBrand(), updatedProduct.getBrand());
            assertEquals(product.getCategory(), updatedProduct.getCategory());
            assertEquals(product.getImage(), updatedProduct.getImage());
        }

        @Test
        void update_NoChanges_ThrowsDataIntegrityViolationException(){
            Product noChangeProduct = new Product();
            noChangeProduct.setId(product.getId());

            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

            NoChangeException exception = assertThrows(NoChangeException.class, () -> productService.update(noChangeProduct));
            assertEquals("변경된 상품 정보가 없습니다.", exception.getMessage());
        }

        @Test
        void update_NonExistingProduct_ThrowsIllegalArgumentException(){
            updateProduct.setId(999L);

            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            IdNotFoundException exception = assertThrows(IdNotFoundException.class, () -> productService.update(updateProduct));
            assertEquals("999(으)로 등록된 상품이 없습니다.", exception.getMessage());
        }

        @Test
        void update_InvalidStockUpdate_ThrowsDataIntegrityViolationException() {
            updateProduct.setStock(-12L);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> productService.update(updateProduct));
            assertEquals("재고가 음수일 수 없습니다.", exception.getMessage());
        }
    }

    @Nested
    class DeleteByIdTests {
        @Test
        void deleteById_ExistingProduct_DeletesSuccessfully() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.deleteById(1L);

            verify(productRepository).deleteById(1L);
        }

        @Test
        void deleteById_NonExistingProduct_ThrowsIllegalArgumentException() {
            when(productRepository.findById(234L)).thenReturn(Optional.empty());

            IdNotFoundException exception = assertThrows(IdNotFoundException.class, () -> productService.deleteById(234L));
            assertEquals("234(으)로 등록된 상품이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    class IncreaseStockTests {
        @Test
        void increaseStock_ExistingProduct_ReturnsUpdatedProduct() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);

            product.setStock(2L);

            Product updatedStock = productService.increaseStock(1L, 15L);

            verify(productRepository).save(product);
            assertEquals(17L, product.getStock());
            assertEquals(17L, updatedStock.getStock());
        }

        @Test
        void increaseStock_NegativeInput_ThrowsDataIntegrityViolationException() {
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> productService.increaseStock(1L, -5L));
            assertEquals("재고가 음수일 수 없습니다.", exception.getMessage());
        }

        @Test
        void increaseStock_NonExistingProduct_ThrowsIllegalArgumentException() {
            when(productRepository.findById(123L)).thenReturn(Optional.empty());

            IdNotFoundException exception = assertThrows(IdNotFoundException.class, () -> productService.increaseStock(123L, 14L));
            assertEquals("123(으)로 등록된 상품이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    class DecreaseStockTests {
        @Test
        void decreaseStock_ExistingProduct_ReturnsUpdatedProduct() {
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
        void decreaseStock_NonExistingProduct_ThrowsIllegalArgumentException() {
            when(productRepository.findById(123L)).thenReturn(Optional.empty());

            IdNotFoundException exception = assertThrows(IdNotFoundException.class, () -> productService.decreaseStock(123L, 14L));
            assertEquals("123(으)로 등록된 상품이 없습니다.", exception.getMessage());
        }
    }
}
