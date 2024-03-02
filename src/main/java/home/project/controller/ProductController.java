package home.project.controller;

import home.project.domain.Product;
import home.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("productRecom")
    public List<String> productRecom() {
        List<String> product;
        product = productService.findProductRecom();
        return product;
    }

    @PostMapping("brandList")
    public List<String> brandList() {
        return productService.brandList();
    }

    @PostMapping("product")
    public ResponseEntity<?> createProduct(@RequestBody @Valid Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
        }

        productService.join(product);
        return ResponseEntity.ok(product);
    }

    @PutMapping("product")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
        }

        productService.update(product);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("product")
    public ResponseEntity<?> deleteProduct(@RequestBody @Valid Product product, BindingResult bindingResult) {
        productService.delete(product);
        return ResponseEntity.ok(product);
    }
}
