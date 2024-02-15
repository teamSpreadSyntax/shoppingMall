package home.project.controller;

import home.project.domain.Product;
import home.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {
    private final ProductService productService;
    @Autowired
    public ProductController(ProductService productService){        this.productService=productService;    }

    @PostMapping("productRecom")
    public List<Product> productRecom(@RequestParam("productRecom")Product product){
        productService.findProductRecom(product);
        return List.of(product);
    }
}
