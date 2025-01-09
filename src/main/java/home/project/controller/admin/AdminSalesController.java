package home.project.controller.admin;

import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.ProductResponseForManager;
import home.project.response.CustomResponseEntity;
import home.project.service.product.ProductService;
import home.project.service.util.PageUtil;
import home.project.service.util.StringBuilderUtil;
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

@Tag(name = "판매", description = "판매관련 API입니다")
@RequestMapping("/api/admin/sales")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class AdminSalesController {

    private final ProductService productService;
    private final PageUtil pageUtil;


    @Operation(summary = "판매 통합 조회 메서드", description = "브랜드명, 카테고리명, 상품명, 일반 검색어, 색상, 사이즈로 판매 내역을 조회합니다. 모든 조건을 만족하는 판매 내역을 조회합니다. 검색어가 없으면 전체 판매 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedSoldProductListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/sold_products")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> searchSoldProducts(
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "colors", required = false) String colors,
            @RequestParam(value = "sizes", required = false) String sizes,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "soldQuantity", direction = Sort.Direction.DESC)
            }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);

        Page<ProductResponseForManager> productPage = productService.findSoldProducts(brand, category, productName, content, colors, sizes, pageable);

        String successMessage = StringBuilderUtil.buildProductSearchCriteria(brand, category, productName, content, productPage);

        long totalCount = productPage.getTotalElements();
        int page = productPage.getNumber();

        return new CustomResponseEntity<>(productPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }


    @Operation(summary = "판매순위 조회 메서드", description = "판매수량이 많은순으로 상품이 조회됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedProductListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/sales_ranking")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> salesRanking(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "soldQuantity", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ProductResponse> productPage = productService.findAllBySoldQuantity(pageable);

        long totalCount = productPage.getTotalElements();

        int page = productPage.getNumber();

        String successMessage = "판매수량이 많은순으로 상품이 조회됩니다.";

        return new CustomResponseEntity<>(productPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);

    }
}




















