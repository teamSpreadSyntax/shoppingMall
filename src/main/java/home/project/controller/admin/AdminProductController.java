package home.project.controller.admin;


import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import home.project.dto.responseDTO.*;
import home.project.response.CustomResponseEntity;
import home.project.service.product.CategoryService;
import home.project.service.product.ProductService;
import home.project.service.util.PageUtil;
import home.project.service.util.StringBuilderUtil;
import home.project.service.validation.ValidationCheck;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Tag(name = "관리자 상품", description = "관리자 상품 관련 API입니다")
@RequestMapping(path = "/api/admin/product")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class AdminProductController {

    private final PageUtil pageUtil;
    private final ProductService productService;
    private final ValidationCheck validationCheck;
    private final CategoryService categoryService;


    @Operation(summary = "상품 등록 메서드", description = "상품과 상세 이미지를 등록하는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))

    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createProduct(
            @RequestPart(value = "productData") @Valid CreateProductRequestDTO createProductRequestDTO,
            @RequestPart(value = "mainImageFile", required = false) MultipartFile mainImageFile,
            @RequestPart(value = "descriptionImages", required = false) MultipartFile[] descriptionImages,
            BindingResult bindingResult) {

        CustomResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;

        List<MultipartFile> imageList = descriptionImages != null ?
                Arrays.asList(descriptionImages) : new ArrayList<>();

        productService.join(createProductRequestDTO, mainImageFile, imageList);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", createProductRequestDTO.getName() + "(이)가 등록되었습니다.");
        return new CustomResponseEntity<>(responseMap, "상품 등록 성공", HttpStatus.OK);
    }

    @Operation(summary = "id로 상품 조회 메서드", description = "id로 상품 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductWithQnAAndReviewResponseForManagerSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/product")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findProductByIdForManaging(@RequestParam("productId") Long productId) {
        ProductWithQnAAndReviewResponseForManager productResponseForManager = productService.findByIdReturnProductResponseForManager(productId);
        String successMessage = productId + "에 해당하는 상품 입니다.";
        return new CustomResponseEntity<>(productResponseForManager, successMessage, HttpStatus.OK);
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
    public ResponseEntity<?> findAllProductForManaging(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "brand", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ProductSimpleResponseForManager> productPage = productService.findAllByIdReturnProductResponseForManager(pageable);

        long totalCount = productPage.getTotalElements();

        int page = productPage.getNumber();

        String successMessage = "귀 판매자가 관리하는 전체 상품입니다.";

        return new CustomResponseEntity<>(productPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "신상품 조회 메서드", description = "신상품 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedNewProductListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
            content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/newProduct")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findNewProduct(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "brand", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ProductSimpleResponseForManager> productPage = productService.adminFindNewProduct(pageable);

        long totalCount = productPage.getTotalElements();

        int page = productPage.getNumber();

        String successMessage = "가장 최근 추가된 신상품 20개입니다.";

        return new CustomResponseEntity<>(productPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "상품 통합 조회", description = "브랜드명, 카테고리명, 상품명 및 일반 검색어로 상품을 조회합니다. 모든 조건을 만족하는 상품을 조회합니다.")
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
    public ResponseEntity<?> searchProductsForManaging(
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "content", required = false) String content,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);

        Page<ProductResponseForManager> productPage = productService.findProductsOnElasticForAdmin(brand, category, productName, content, pageable);

        String successMessage = StringBuilderUtil.buildProductSearchCriteria(brand, category, productName, content, productPage);

        long totalCount = productPage.getTotalElements();
        int page = productPage.getNumber();

        return new CustomResponseEntity<>(productPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);

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
    public ResponseEntity<?> brandList(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {

        pageable = pageUtil.pageable(pageable);
        Page<String> pagedBrands = productService.brandList(pageable);
        String successMessage = "전체 브랜드 입니다.";
        long totalCount = pagedBrands.getTotalElements();
        int page = pagedBrands.getNumber();
        return new CustomResponseEntity<>(pagedBrands.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "상품 업데이트(수정) 메서드", description = "업데이트(수정) 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateProduct(
            @RequestPart(value = "productData") @Valid UpdateProductRequestDTO updateProductRequestDTO,
            @RequestPart(value = "mainImageFile", required = false) MultipartFile mainImageFile,
            @RequestPart(value = "descriptionImages", required = false) MultipartFile[] descriptionImages,
            BindingResult bindingResult) {
        CustomResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;

        List<MultipartFile> imageList = descriptionImages != null ?
                Arrays.asList(descriptionImages) : new ArrayList<>();

        ProductResponse productResponse = productService.updateMyProduct(updateProductRequestDTO ,mainImageFile , imageList);
        String successMessage = "상품 정보가 수정되었습니다.";
        return new CustomResponseEntity<>(productResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "상품 삭제 메서드", description = "상품 삭제 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("/delete")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteProduct(@RequestParam("productId") Long productId) {
        String name = productService.deleteByIdForAdmin(productId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", name + "(id:" + productId + ")(이)가 삭제되었습니다.");
        return new CustomResponseEntity<>(responseMap, "상품 삭제 성공", HttpStatus.OK);
    }

    @Operation(summary = "재고 수량 증가 메서드", description = "증가 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseForManagerSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @PutMapping("/increase_stock")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> increaseStock(@RequestParam("productId") Long productId, @RequestParam("stock") Long stock) {
        ProductResponseForManager productResponseForManager = productService.increaseStockForAdmin(productId, stock);
        String successMessage = productResponseForManager.getName() + "상품이 " + stock + "개 증가하여 " + productResponseForManager.getStock() + "개가 되었습니다.";
        return new CustomResponseEntity<>(Optional.of(productResponseForManager), successMessage, HttpStatus.OK);

    }

    @Transactional
    @Operation(summary = "재고 수량 감소 메서드", description = "재고 수량 감소 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseForManagerSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))


    })
    @PutMapping("/decrease_stock")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> decreaseStock(@RequestParam("productId") Long productId, @RequestParam("stock") Long stock) {
        ProductResponseForManager decreaseProduct = productService.decreaseStockForAdmin(productId, stock);
        String successMessage = decreaseProduct.getName() + "상품이 " + stock + "개 감소하여 " + decreaseProduct.getStock() + "개가 되었습니다.";
        return new CustomResponseEntity<>(Optional.of(decreaseProduct), successMessage, HttpStatus.OK);
    }
}
