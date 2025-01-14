package home.project.controller.user;

import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.WishListResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.order.WishListService;
import home.project.service.util.PageUtil;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "WishList", description = "WishList 관련 API입니다")
@RequestMapping("/api/wishList")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class WishListController {

    private final WishListService wishListService;
    private final PageUtil pageUtil;

    @Operation(summary = "WishList 등록 메서드", description = "WishList 등록 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product added to wishlist",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/WishListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid product ID",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))

    })
    @PostMapping("/add")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> addWishList(@RequestParam("productId") Long productId) {
        WishListResponse wishListResponse = wishListService.addToWishList(productId);
        return new CustomResponseEntity<>(wishListResponse, wishListResponse.getMessage(), HttpStatus.OK);
    }

    @Operation(summary = "WishList 삭제 메서드", description = "WishList 삭제 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product removed from wishlist",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Product not found in wishlist",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @DeleteMapping("/remove")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> removeWishList(@RequestParam("productId") Long productId) {
        WishListResponse wishListResponse = wishListService.removeFromWishList(productId);
        return new CustomResponseEntity<>(wishListResponse, wishListResponse.getMessage(), HttpStatus.OK);
    }

    @Operation(summary = "내 wishList 조회 메서드", description = "내 wishList 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wishlist retrieved successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedWishListResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Wishlist not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/my_wishList")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findMyWishList(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "wishListId", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ProductResponse> pagedWishList = wishListService.findAllMyWishList(pageable);

        long totalCount = pagedWishList.getTotalElements();
        int page = pagedWishList.getNumber();

        return new CustomResponseEntity<>(pagedWishList.getContent(), "내 모든 wishList 입니다.", HttpStatus.OK, totalCount, page);
    }
}