package home.project.controller;


import home.project.domain.*;
import home.project.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Tag(name = "상품", description = "상품관련 API 입니다")
@RequestMapping(path = "/api/product")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "403", description = "접근이 금지되었습니다.", content = @Content(schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "404", description = "요청한 리소스를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = Product.class)))
})
@RestController
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "상품추가 메서드", description = "상품추가 메서드입니다.")
    @PostMapping("CreateProduct")
    public CustomOptionalResponseEntity<?> createProduct(@RequestBody @Valid ProductDTOWithoutId productDTOWithoutId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> responseMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                responseMap.put(error.getField(), error.getDefaultMessage());
            }
            CustomOptionalResponseBody<Optional<Product>> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap), "Validation failed", HttpStatus.BAD_REQUEST.value());
            return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
            Product product = new Product();
            product.setBrand(productDTOWithoutId.getBrand());
            product.setCategory(productDTOWithoutId.getCategory());
            product.setSelledcount(productDTOWithoutId.getSelledcount());
            product.setName(productDTOWithoutId.getName());
            product.setStock(productDTOWithoutId.getStock());
            product.setImage(productDTOWithoutId.getImage());
            productService.join(product);
            Long currentStock = productDTOWithoutId.getStock();
             if (currentStock < 0){
            throw new DataIntegrityViolationException("재고가 음수 일 수 없습니다.");}
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("상품등록완료", product.getName() + "가 등록되었습니다");
            CustomOptionalResponseBody<Optional<Product>> responseBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap), "상품등록 성공", HttpStatus.OK.value());
            return new CustomOptionalResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @Operation(summary = "상품 통합 조회 메서드", description = "브랜드명, 카테고리명, 상품명 및 일반 검색어로 상품을 조회합니다. 모든 조건을 만족하는 상품을 조회합니다. 검색어가 없으면 전체 상품을 조회합니다.")
    @GetMapping("/searchProducts")
    public ResponseEntity<?> searchProducts(
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "query", required = false) String query,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        Page<Product> productPage = productService.findProducts(brand, category, productName, query, pageable);
        String successMessage = "검색 결과입니다";
        long totalCount = productPage.getTotalElements();
        int page = productPage.getNumber();
        return new CustomListResponseEntity<>(productPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "ID로 상품조회 메서드", description = "ID로 상품조회 메서드입니다")
    @GetMapping("FindProductById")
    public CustomOptionalResponseEntity<Optional<Product>> findProductById(@RequestParam("ID") Long ID) {
        if (ID == null) { throw new IllegalStateException("id가 입력되지 않았습니다.");  }
            Optional<Product> product = productService.findById(ID);
            String successMessage = ID+"에 해당하는 상품 입니다";
            return new CustomOptionalResponseEntity<>(Optional.ofNullable(product), successMessage, HttpStatus.OK);
    }

    @Operation(summary = "상품업데이트(수정) 메서드", description = "상품업데이트(수정) 메서드입니다.")
    @PutMapping("UpdateProduct")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> responseMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                responseMap.put(error.getField(), error.getDefaultMessage());
            }
            CustomOptionalResponseBody<Optional<Product>> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap), "Validation failed", HttpStatus.BAD_REQUEST.value());
            return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
            Optional<Product> productOptional = productService.update(product);
            String successMessage = "상품정보가 수정되었습니다";
            return new CustomOptionalResponseEntity<>(Optional.ofNullable(productOptional),successMessage, HttpStatus.OK);

    }

    @Transactional
    @Operation(summary = "상품삭제 메서드", description = "상품삭제 메서드입니다.")
    @DeleteMapping("DeleteProduct")
    public ResponseEntity<?> deleteProduct(@RequestParam("productId") Long productId) {
            productService.deleteById(productId);
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("상품삭제 완료", productId+"가 삭제되었습니다");
            CustomOptionalResponseBody responseBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap),"상품삭제 성공", HttpStatus.OK.value());
            return new CustomOptionalResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @Operation(summary = "전체브랜드조회 메서드", description = "브랜드조회(판매량기준 오름차순정렬) 메서드입니다.")
    @GetMapping("brandList")
    public CustomListResponseEntity<Product> brandList(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        Page<Product> brandListPage = productService.brandList(pageable);
        String successMessage = "전체 브랜드 입니다";
        long totalCount = brandListPage.getTotalElements();
        int page = brandListPage.getNumber();

        return new CustomListResponseEntity<>(brandListPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "재고수량 증가 메서드", description = "재고수량 증가 메서드 입니다.")
    @PutMapping ("IncreaseStock")
    public CustomOptionalResponseEntity<Product> increaseStock(@RequestParam("productId") Long productId, @RequestParam("stock") Long stock){
            Product increaseProduct = productService.increaseStock(productId,stock);
            String successMessage = increaseProduct.getName()+"상품이"+stock+"개 증가하여"+increaseProduct.getStock()+"개가 되었습니다";
            return new CustomOptionalResponseEntity<>(Optional.of(increaseProduct),successMessage, HttpStatus.OK);
    }

    @Operation(summary = "재고수량 감소 메서드", description = "재고수량 감소 메서드 입니다.")
    @PutMapping("DecreaseStock")
    public CustomOptionalResponseEntity<Product> decreaseStock(@RequestParam("productId") Long productId, @RequestParam("stock") Long stock){
            Product decreaseProduct = productService.decreaseStock(productId,stock);
            String successMessage = decreaseProduct.getName()+"상품이"+stock+"개 감소하여"+decreaseProduct.getStock()+"개가 되었습니다";
            return new CustomOptionalResponseEntity<>(Optional.of(decreaseProduct),successMessage, HttpStatus.OK);
    }

}
