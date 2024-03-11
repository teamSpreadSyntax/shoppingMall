package home.project.controller;


import home.project.domain.Product;
import home.project.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "상품", description = "상품관련 API 입니다")
@RequestMapping(path = "/api/product")
@RestController
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "상품추가 메서드", description = "상품추가 메서드입니다.")
    @PostMapping("CreateProduct")
    public ResponseEntity<?> createProduct(@RequestBody @Valid  Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
        }try {
            productService.join(product);
            return ResponseEntity.ok(product);
        }catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "전체상품조회 메서드", description = "전체상품조회 메서드입니다.")
    @GetMapping("FindAllProduct")
    public ResponseEntity<List<Product>> findAllProduct() {
        List<Product> productList = productService.findAll();
        return ResponseEntity.ok(productList);
    }

    @Operation(summary = "상품명으로상품조회 메서드", description = "상품명으로상품조회 메서드입니다.")
    @GetMapping("FindByName")
    public ResponseEntity<Optional<Product>> findProductByName(@RequestParam("ProductName") String name) {
        Optional<Product> product = productService.findByname(name);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "카테고리별상품조회 메서드", description = "카테고리별상품조회 메서드입니다.")
    @GetMapping("FindByCategory")
    public ResponseEntity<Optional<List<Product>>> findProductByCategory(@RequestParam("category") String category) {
        Optional<List<Product>> product = productService.findByCategory(category);
        return ResponseEntity.ok(product);
    }
//            0101(상의반팔)
//            0102(상의긴팔)
//            0201(바지긴바지)
//            0202(바지반바지)

    @Operation(summary = "상품업데이트(수정) 메서드", description = "상품업데이트(수정) 메서드입니다.")
    @PutMapping("UpdateProduct")
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

    @Operation(summary = "상품상세 메서드", description = "상품상세 메서드입니다.")
    @GetMapping("DetailProduct")
    public ResponseEntity<Optional<Product>> DetailProduct(@RequestParam("productId") Long productid) {
        Optional<Product> productdetail = productService.DetailProduct(productid);
        return ResponseEntity.ok(productdetail);
    }

    @Operation(summary = "상품삭제 메서드", description = "상품삭제 메서드입니다.")
    @DeleteMapping("DeleteProduct")
    public ResponseEntity<?> deleteProduct(@RequestParam("productId") Long productid) {
        productService.deleteById(productid);
        return ResponseEntity.ok(productid);
    }

    @Operation(summary = "전체브랜드조회 메서드", description = "브랜드조회(판매량기준 오름차순정렬) 메서드입니다.")
    @GetMapping("brandList")
    public List<String> brandList() {
        return productService.brandList();
    }
}
