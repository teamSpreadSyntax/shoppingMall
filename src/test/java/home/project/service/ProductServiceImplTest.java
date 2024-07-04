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

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Product product2;
    private Product product3;

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
        product2.setId(2L);
        product2.setBrand("aidias");
        product2.setName("트레이닝");
        product2.setCategory("바지");
        product2.setStock(0L);
        product2.setImage("abcd.jpg");

        product3 = new Product();
        product3.setId(3L);
        product3.setBrand("puma");
        product3.setName("트레이닝 트랙");
        product3.setCategory("바지");
        product3.setStock(4L);
        product3.setImage("abcadfadd.jpg");

    }
    @Test
    void 제품등록_성공() {
        productService.join(product3);
        verify(productRepository).save(product3);
        assertEquals("puma", product3.getBrand());
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
    void 특정_조건_검색() {
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
    void 특정_조건_검색_예외() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> emptyPage = Page.empty(pageable);

        when(productRepository.findProducts("1","2","3",null,pageable)).thenReturn(emptyPage);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> productService.findProducts("1","2","3",null, pageable));
        assertEquals("해당하는 상품이 없습니다.",exception.getMessage());
    }

    @Test
    void 브랜드_리스트() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(product, product2);
        Page<Product> page = new PageImpl<>(products, pageable, products.size());
        when(productRepository.findAllByOrderByBrandAsc(pageable)).thenReturn(page);

        Page<Product> result = productService.brandList(pageable);
        assertEquals(2, result.getTotalElements());
        assertEquals(products, result.getContent());
    }

    @Test
    void 업데이트_성공() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        product.setBrand("PUMA");
        product.setStock(15L);

        Optional<Product> updatedProduct = productService.update(product);

        verify(productRepository).save(product);
        assertTrue(updatedProduct.isPresent());
        assertEquals("PUMA", updatedProduct.get().getBrand());
        assertEquals(15L, updatedProduct.get().getStock());
    }

    @Test
    void 재고_음수_업데이트_예외() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        product.setBrand("PUMA");
        product.setStock(-15L);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> productService.update(product));
        assertEquals("재고가 음수 일 수 없습니다.",exception.getMessage());
    }

    @Test
    void Id_제품_삭제() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteById(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void Id_제품_삭제_예외() {
        when(productRepository.findById(234L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class , () -> productService.deleteById(234L));
        assertEquals("234로 등록된 상품이 없습니다.", exception.getMessage());
    }

    @Test
    void 재고_증가_성공() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        product.setStock(2L);

        Product updatedStock = productService.increaseStock(1L, 15L);

        verify(productRepository).save(product);
        assertEquals(17L, product.getStock());
        assertEquals(17L, updatedStock.getStock());
    }

    @Test
    void 재고_증가_음수() {

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> productService.increaseStock(1L, -5L));
        assertEquals("재고가 음수일 수 없습니다.", exception.getMessage());
    }
}
