package home.project.controller;

import home.project.domain.Product;
import home.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    private final ProductService productService;
    @Autowired
    public ProductController(ProductService productService){
        this.productService=productService;
    }

    @PostMapping("productRecom")
    public List<String> productRecom(){
        List<String> product;
        product = productService.findProductRecom();
        return product;
    }

    @PostMapping("brandList")
    public List<String> brandList(){
        return productService.brandList();
    }

    @PostMapping("createProduct")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        productService.join(product);
        return ResponseEntity.ok(product);
    }
}
