package home.project.service;

import home.project.domain.Product;
import home.project.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Product product2;

    @BeforeEach
    public void setUp() {
        product = new Product();
        product.setId(1L);
        product.setBrand("NIKE");
        product.setName("에어맥스");
        product.setCategory("신발");
        product.setStock(0L);
        product.setImage("abc.jpg");

        product2 = new Product();
        product.setId(2L);
        product2.setBrand("aidias");
        product2.setName("트레이닝");
        product2.setCategory("바지");
        product2.setStock(0L);
        product2.setImage("abcd.jpg");
    }
    @Test
    void 제품등록_성공() {
        productService.join(product);
        verify(productRepository).save(product);
        assertEquals("NIKE", product.getBrand());
    }

    @Test
    void ID_찾기_성공() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> findProduct = productService.findById(1L);

        assertTrue(findProduct.isPresent());
        assertEquals(product, findProduct.get());
    }

    @Test
    void  ID_찾기_예외() {
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> productService.findById(3L));
        assertEquals("3로 등록된 상품이 없습니다.", exception.getMessage());
    }

    @Test
    void 모든_제품_검색() {
        Pageable pageable = PageRequest.of(0,10);
        List<Product> productList = Arrays.asList(product, product2);
        Page<Product> page = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(page);
        Page<Product> result = productService.findAll(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(productList, result.getContent());
    }

    @Test
    void 제품_개별_검색() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Product> pantsProducts = Arrays.asList(product2);
        Page<Product> pantsProductsPage = new PageImpl<>(pantsProducts, pageable, pantsProducts.size());

        when(productRepository.findProducts(null, "바", null, null, pageable)).thenReturn(pantsProductsPage);

        Page<Product> result = productService.findProducts(null, "바", null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("바지", result.getContent().get(0).getCategory());
        assertEquals(product2, result.getContent().get(0));
    }
}
