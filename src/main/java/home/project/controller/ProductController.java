package home.project.controller;


import home.project.domain.*;
import home.project.dto.CategoryDTOWithoutId;
import home.project.dto.ProductDTOWithoutId;
import home.project.response.CustomListResponseEntity;
import home.project.response.CustomOptionalResponseEntity;
import home.project.service.CategoryService;
import home.project.service.ProductService;
import home.project.util.CategoryCode;
import home.project.util.ValidationCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Tag(name = "상품", description = "상품관련 API입니다")
@RequestMapping(path = "/api/product")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;
    private final ValidationCheck validationCheck;
    private final CategoryService categoryService;


    @Operation(summary = "상품 등록 메서드", description = "상품 등록 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),

    })
    @Transactional
    @PostMapping("/create")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER')")
    public ResponseEntity<?> createProduct(@RequestBody @Valid ProductDTOWithoutId productDTOWithoutId, BindingResult bindingResult) {
        CustomOptionalResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);

        if (validationResponse != null) return validationResponse;

        productService.join(productDTOWithoutId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", productDTOWithoutId.getName() + "(이)가 등록되었습니다.");
        return new CustomOptionalResponseEntity<>(Optional.of(responseMap), "상품 등록 성공", HttpStatus.OK);

    }

    @Operation(summary = "카테고리 등록 메서드", description = "카테고리 등록 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),

    })
    @Transactional
    @PostMapping("/createCategory")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER')")
    public ResponseEntity<?> createCategory(@RequestBody @Valid CategoryDTOWithoutId category, BindingResult bindingResult) {
        CustomOptionalResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;
        categoryService.save(category);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", category.getName() + "(이)가 등록되었습니다.");
        return new CustomOptionalResponseEntity<>(Optional.of(responseMap), "상품 등록 성공", HttpStatus.OK);
    }

    @Operation(summary = "id로 상품 조회 메서드", description = "id로 상품 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/product")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER', 'ROLE_USER')")
    public ResponseEntity<?> findProductById(@RequestParam("productId") Long productId) {
        Optional<Product> productOptional = productService.findById(productId);
        String successMessage = productId + "에 해당하는 상품 입니다.";
        return new CustomOptionalResponseEntity<>(productOptional, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체 상품 조회 메서드", description = "전체 상품 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedProductListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/products")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER', 'ROLE_USER')")
    public ResponseEntity<?> findAllProduct(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {

        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        Page<Product> productList = productService.findAll(pageable);
        String successMessage = "전체 상품입니다.";
        long totalCount = productList.getTotalElements();
        int page = productList.getNumber();
        return new CustomListResponseEntity<>(productList.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "상품 통합 조회 메서드", description = "브랜드명, 카테고리명, 상품명 및 일반 검색어로 상품을 조회합니다. 모든 조건을 만족하는 상품을 조회합니다. 검색어가 없으면 전체 상품을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedProductListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/search")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER', 'ROLE_USER')")
    public ResponseEntity<?> searchProducts(
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "content", required = false) String content,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());

        String categoryCode = CategoryCode.generateCategoryCode(category, content);
        Page<Product> productPage = productService.findProducts(brand, categoryCode, productName, content, pageable);
        String successMessage = productService.stringBuilder(brand, category, productName, content, productPage);

        long totalCount = productPage.getTotalElements();
        int page = productPage.getNumber();

        return new CustomListResponseEntity<>(productPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);

    }

    @Operation(summary = "전체 브랜드 조회 메서드", description = "브랜드 조회(판매량기준 오름차순정렬) 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BrandListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/brands")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER', 'ROLE_USER')")
    public ResponseEntity<?> brandList(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {

        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        Page<Product> brandListPage = productService.brandList(pageable);
        String successMessage = "전체 브랜드 입니다.";
        long totalCount = brandListPage.getTotalElements();
        int page = brandListPage.getNumber();
        return new CustomListResponseEntity<>(brandListPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);

    }

    @Transactional
    @Operation(summary = "상품 업데이트(수정) 메서드", description = "상품 업데이트(수정) 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseSchema"))),
            @ApiResponse(responseCode = "204", description = "NO_CONTENT",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NochangeResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
    })
    @PutMapping("/update")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER', 'ROLE_USER')")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid Product product, BindingResult bindingResult) {
        CustomOptionalResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;
        Optional<Product> productOptional = productService.update(product);
        String successMessage = "상품 정보가 수정되었습니다.";
        return new CustomOptionalResponseEntity<>(productOptional, successMessage, HttpStatus.OK);
    }

    @Transactional
    @Operation(summary = "상품 삭제 메서드", description = "상품 삭제 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("/delete")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER')")
    public ResponseEntity<?> deleteProduct(@RequestParam("productId") Long productId) {
        String name = productService.findById(productId).get().getName();
        productService.deleteById(productId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", name + "(id:" + productId + ")(이)가 삭제되었습니다.");
        return new CustomOptionalResponseEntity<>(Optional.of(responseMap), "상품 삭제 성공", HttpStatus.OK);
    }

    @Transactional
    @Operation(summary = "재고 수량 증가 메서드", description = "재고 수량 증가 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PutMapping("/increase_stock")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER')")
    public ResponseEntity<?> increaseStock(@RequestParam("productId") Long productId, @RequestParam("stock") Long stock) {
        Product increaseProduct = productService.increaseStock(productId, stock);
        String successMessage = increaseProduct.getName() + "상품이 " + stock + "개 증가하여 " + increaseProduct.getStock() + "개가 되었습니다.";
        return new CustomOptionalResponseEntity<>(Optional.of(increaseProduct), successMessage, HttpStatus.OK);

    }

    @Transactional
    @Operation(summary = "재고 수량 감소 메서드", description = "재고 수량 감소 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ConflictResponseSchema")))

    })
    @PutMapping("/decrease_stock")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER')")
    public ResponseEntity<?> decreaseStock(@RequestParam("productId") Long productId, @RequestParam("stock") Long stock) {
        Product decreaseProduct = productService.decreaseStock(productId, stock);
        String successMessage = decreaseProduct.getName() + "상품이 " + stock + "개 감소하여 " + decreaseProduct.getStock() + "개가 되었습니다.";
        return new CustomOptionalResponseEntity<>(Optional.of(decreaseProduct), successMessage, HttpStatus.OK);
    }
}
