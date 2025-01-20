package home.project.controller.user;

import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.ProductSimpleResponse;
import home.project.dto.responseDTO.ProductWithQnAAndReviewResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.product.CategoryService;
import home.project.service.product.ProductService;
import home.project.service.util.PageUtil;
import home.project.service.util.StringBuilderUtil;
import home.project.service.util.ValidationCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상품", description = "상품 관련 API입니다.")
@RequestMapping(path = "/api/product")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class ProductController {

    private final PageUtil pageUtil;
    private final ProductService productService;
    private final ValidationCheck validationCheck;
    private final CategoryService categoryService;


    @Operation(summary = "상품 조회", description = "상품 ID로 상품을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/product")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findProductById(@RequestParam("productId") Long productId) {
        ProductWithQnAAndReviewResponse productResponse = productService.findByIdReturnProductResponse(productId);
        String successMessage = productId + "에 해당하는 상품입니다.";
        return new CustomResponseEntity<>(productResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체 상품 조회", description = "전체 상품을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 상품 조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/productSimpleResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/products")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findAllProduct(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "brand", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ProductSimpleResponse> productPage = productService.findAll(pageable);

        long totalCount = productPage.getTotalElements();

        int page = productPage.getNumber();

        String successMessage = "전체 상품입니다.";

        return new CustomResponseEntity<>(productPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "신상품 조회", description = "신상품 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신상품 조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/productSimpleResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/newProduct")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findNewProduct(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "brand", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ProductSimpleResponse> productPage = productService.findNewProduct(pageable);

        long totalCount = productPage.getTotalElements();

        int page = productPage.getNumber();

        String successMessage = "가장 최근 추가된 신상품입니다.";

        return new CustomResponseEntity<>(productPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "상품 통합 조회", description = "상품명, 브랜드명, 카테고리명 등으로 상품을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 통합 조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/productSimpleResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/search")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> searchProducts(
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "content", required = false) String content,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);

        Page<ProductSimpleResponse> productPage = productService.findProductsOnElastic(brand, category, productName, content, pageable);

        String successMessage = StringBuilderUtil.buildProductSearchCriteria(brand, category, productName, content, productPage);

        long totalCount = productPage.getTotalElements();
        int page = productPage.getNumber();

        return new CustomResponseEntity<>(productPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);

    }

    @Operation(summary = "브랜드 조회", description = "브랜드 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "브랜드 조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedBrandListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/brands")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> brandList(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {

        pageable = pageUtil.pageable(pageable);
        Page<String> pagedBrands = productService.brandList(pageable);
        String successMessage = "전체 브랜드입니다.";
        long totalCount = pagedBrands.getTotalElements();
        int page = pagedBrands.getNumber();
        return new CustomResponseEntity<>(pagedBrands.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

}
