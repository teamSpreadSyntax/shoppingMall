package home.project.controller;

import home.project.SecurityConfig;
import home.project.domain.Product;
import home.project.domain.ProductDTOWithoutId;
import home.project.service.JwtTokenProvider;
import home.project.service.ProductService;
import home.project.service.ValidationCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SecurityConfig.class)
})
@Import({SecurityConfig.class, ProductControllerTest.TestConfig.class})
@EnableWebSecurity
public class ProductControllerTest {

    @Configuration
    static class TestConfig {
        @MockBean
        public UserDetailsService userDetailsService;

        @MockBean
        public PasswordEncoder passwordEncoder;

        @MockBean
        public JwtTokenProvider jwtTokenProvider;

        @Bean
        public SecurityConfig securityConfig(JwtTokenProvider jwtTokenProvider) {
            return new SecurityConfig(jwtTokenProvider);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ValidationCheck validationCheck;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateProduct_Success() throws Exception {
        ProductDTOWithoutId productDTO = new ProductDTOWithoutId();
        productDTO.setBrand("Test Brand");
        productDTO.setCategory("Test Category");
        productDTO.setSelledcount(10L);
        productDTO.setName("Test Product");
        productDTO.setStock(100L);
        productDTO.setImage("test_image.jpg");

        when(validationCheck.validationChecks(bindingResult)).thenReturn(null);

        Product product = new Product();
        product.setId(1L);
        product.setBrand(productDTO.getBrand());
        product.setCategory(productDTO.getCategory());
        product.setSelledcount(productDTO.getSelledcount());
        product.setName(productDTO.getName());
        product.setStock(productDTO.getStock());
        product.setImage(productDTO.getImage());

        doNothing().when(productService).join(any(Product.class));

        mockMvc.perform(post("/api/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"brand\": \"Test Brand\", \"category\": \"Test Category\", \"selledcount\": 10, \"name\": \"Test Product\", \"stock\": 100, \"image\": \"test_image.jpg\" }"))
                .andExpect(status().isOk());
    }
}
