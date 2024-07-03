package home.project.service;

import home.project.domain.Product;
import home.project.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//테스트 환경에서 데이터베이스 자동으로 설정
@Transactional
//테스트가 끝난 후 데이터베이스 변경사항 롤백
class ProductServiceTest {
    @LocalServerPort
    int port;

    @Autowired ProductService productService;
    @Autowired ProductRepository productRepository;

    @BeforeEach
    public void clearData(){
        productRepository.deleteAll();
    }

    @DisplayName("제품등록 테스트 케이스")
    @Test
    void join() {
        // given
        Product product1 = new Product();
        product1.setBrand("qwer");
        product1.setCategory("qert");
        product1.setImage("1234");
        product1.setName("나이키 플러워");
        product1.setStock(4545L);
        // when
        productService.join(product1);
        // then
        Product product2 = productService.findById(product1.getId()).get();
        Assertions.assertThat(product1.getName()).isEqualTo(product2.getName());
    }

    @DisplayName("모든 제품 찾기")
    @Test
    void findAll() {
        // given
        Product product1 = new Product();
        product1.setBrand("나이키");
        product1.setCategory("바지");
        product1.setImage("1234.img");
        product1.setName("나이키 플러워");
        product1.setStock(100L);
        productService.join(product1);

        Product product2 = new Product();
        product2.setBrand("아디다스");
        product2.setCategory("팬츠");
        product2.setImage("123.img");
        product2.setName("축구화");
        product2.setStock(45L);
        productService.join(product2);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = productService.findAll(pageable);

        // then
        Assertions.assertThat(productPage.getTotalElements()).isEqualTo(2);
    }

    @DisplayName("제품 ID로 찾기")
    @Test
    void findById() {
        // given
        Product product = new Product();
        product.setBrand("나이키");
        product.setCategory("바지");
        product.setImage("1234.img");
        product.setName("나이키 플러워");
        product.setStock(100L);
        productService.join(product);

        // when
        Optional<Product> foundProduct = productService.findById(product.getId());

        // then
        Assertions.assertThat(foundProduct.isPresent()).isTrue();
        Assertions.assertThat(foundProduct.get().getName()).isEqualTo("나이키 플러워");
    }

    @DisplayName("존재하지 않는 제품 ID로 찾기")
    @Test
    void findByIdNotFound() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            productService.findById(4L);
        });
    }

    @DisplayName("재고 증가 테스트")
    @Test
    void increaseStock() {
        // given
        Product product = new Product();
        product.setBrand("나이키");
        product.setCategory("바지");
        product.setImage("123.img");
        product.setName("나이키 플러워");
        product.setStock(10L);
        product.setSelledcount(50L);
        productService.join(product);

        // when
        Product updatedProduct = productService.increaseStock(product.getId(), 10L);

        // then
        Assertions.assertThat(updatedProduct.getStock()).isEqualTo(20L);
        Assertions.assertThat(updatedProduct.getSelledcount()).isEqualTo(50L);
    }

    @DisplayName("재고 감소 테스트")
    @Test
    void decreaseStock() {
        // given
        Product product = new Product();
        product.setBrand("나이키");
        product.setCategory("바지");
        product.setImage("123.img");
        product.setName("나이키 플러워");
        product.setStock(10L);
        product.setSelledcount(50L);
        productService.join(product);

        // when
        Product updatedProduct = productService.decreaseStock(product.getId(), 10L);

        // then
        Assertions.assertThat(updatedProduct.getStock()).isEqualTo(0L);
        Assertions.assertThat(updatedProduct.getSelledcount()).isEqualTo(50L);
    }

    @DisplayName("재고 감소 시 재고 부족 예외 테스트")
    @Test
    void decreaseStockInsufficient() {
        // given
        Product product = new Product();
        product.setBrand("나이키");
        product.setCategory("바지");
        product.setImage("123.img");
        product.setName("나이키 플러워");
        product.setStock(10L);
        product.setSelledcount(50L);
        productService.join(product);

        // when & then
        assertThrows(DataIntegrityViolationException.class, () -> {
            productService.decreaseStock(product.getId(), 20L);
        });
    }

    @DisplayName("제품 삭제 테스트")
    @Test
    void deleteById() {
        // given
        Product product = new Product();
        product.setBrand("나이키");
        product.setCategory("바지");
        product.setImage("123.img");
        product.setName("나이키 플러워");
        product.setStock(10L);
        productService.join(product);

        // when
        productService.deleteById(product.getId());

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            productService.findById(product.getId());
        });
    }

    @DisplayName("제품 정보 업데이트 테스트")
    @Test
    void update() {
        // given
        Product product = new Product();
        product.setBrand("나이키");
        product.setCategory("바지");
        product.setImage("123.img");
        product.setName("나이키 플러워");
        product.setStock(10L);
        productService.join(product);

        // when
        product.setCategory("카고바지");
        Optional<Product> updatedProduct = productService.update(product);

        // then
        Assertions.assertThat(updatedProduct.isPresent()).isTrue();
        Assertions.assertThat(updatedProduct.get().getCategory()).isEqualTo("카고바지");
    }

    @DisplayName("브랜드별 제품 목록 찾기 테스트")
    @Test
    void brandList() {
        // given
        Product product1 = new Product();
        product1.setBrand("나이키");
        product1.setCategory("바지");
        product1.setImage("123.img");
        product1.setName("나이키 플러워");
        product1.setStock(10L);
        productService.join(product1);

        Product product2 = new Product();
        product2.setBrand("아디다스");
        product2.setCategory("신발");
        product2.setImage("123456.img");
        product2.setName("아디다스 신발");
        product2.setStock(20L);
        productService.join(product2);

        Product product3 = new Product();
        product3.setBrand("뉴발란스");
        product3.setCategory("바지");
        product3.setImage("12345.img");
        product3.setName("뉴발란스 모자");
        product3.setStock(30L);
        productService.join(product3);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productList = productService.findProducts(null, null, null, null, pageable);

        // then
        Assertions.assertThat(productList.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(productList.getContent())
                .extracting(Product::getBrand)
                .containsExactlyInAnyOrder("나이키", "아디다스", "뉴발란스");
    }
    /*@DisplayName("브랜드별 제품 목록 내림차순 정렬 테스트")
    @Test
    void brandListDescending() {
        // given
        Product product1 = new Product();
        product1.setBrand("나이키");
        product1.setCategory("바지");
        product1.setImage("123.img");
        product1.setName("나이키 플러워");
        product1.setStock(10L);
        productService.join(product1);

        Product product2 = new Product();
        product2.setBrand("아디다스");
        product2.setCategory("신발");
        product2.setImage("123456.img");
        product2.setName("아디다스 신발");
        product2.setStock(20L);
        productService.join(product2);

        Product product3 = new Product();
        product3.setBrand("뉴발란스");
        product3.setCategory("바지");
        product3.setImage("12345.img");
        product3.setName("뉴발란스 모자");
        product3.setStock(30L);
        productService.join(product3);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productList = productService.brandList(pageable);

        // then
        Assertions.assertThat(productList.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(productList.getContent())
                .extracting(Product::getBrand)
                .containsExactly("뉴발란스", "아디다스", "나이키");
    }*/

}
