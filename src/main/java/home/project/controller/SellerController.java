package home.project.controller;

import home.project.dto.requestDTO.CreateSellerRequestDTO;
import home.project.dto.responseDTO.ProductSellerResponse;
import home.project.dto.responseDTO.SellerResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.SellerService;
import home.project.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "판매자", description = "판매자 관련 API입니다")
@RequestMapping(path = "/api/sellers")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class SellerController {

    private final SellerService sellerService;
    private final PageUtil pageUtil;

    @Operation(summary = "판매자 추가", description = "새로운 판매자를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/SellerResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @PostMapping("/add")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createSeller(@Valid @RequestBody CreateSellerRequestDTO requestDTO) {
        SellerResponse sellerResponse = sellerService.createSeller(requestDTO);
        String successMessage = "판매자가 성공적으로 추가되었습니다.";
        return new CustomResponseEntity<>(sellerResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "판매자별 상품 조회", description = "특정 판매자의 상품을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedProductListResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/products")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findProductsBySeller(
            @RequestParam("sellerId") Long sellerId,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {

        pageable = pageUtil.pageable(pageable);
        Page<ProductSellerResponse> productPage = sellerService.findProductsBySeller(sellerId, pageable);

        long totalCount = productPage.getTotalElements();
        int page = productPage.getNumber();
        String successMessage = sellerId + "번 판매자의 전체 상품 목록입니다.";

        return new CustomResponseEntity<>(productPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }
}
