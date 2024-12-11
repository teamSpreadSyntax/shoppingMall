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

@Tag(name = "위시리스트", description = "위시리스트 관련 API입니다.")
@RequestMapping("/api/wishList")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class WishListController {

    private final WishListService wishListService;
    private final PageUtil pageUtil;

    @Operation(summary = "위시리스트 추가", description = "상품을 위시리스트에 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/WishListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "권한 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/add")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> addWishList(@RequestParam("productId") Long productId) {
        WishListResponse wishListResponse = wishListService.addToWishList(productId);
        return new CustomResponseEntity<>(wishListResponse, wishListResponse.getMessage(), HttpStatus.OK);
    }

    @Operation(summary = "위시리스트 삭제", description = "위시리스트에서 상품을 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/WishListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "권한 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("/remove")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> removeWishList(@RequestParam("productId") Long productId) {
        WishListResponse wishListResponse = wishListService.removeFromWishList(productId);
        return new CustomResponseEntity<>(wishListResponse, wishListResponse.getMessage(), HttpStatus.OK);
    }

    @Operation(summary = "내 위시리스트 조회", description = "로그인한 사용자의 위시리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/my_wishList")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findMyWishList(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "wishListId", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ProductResponse> pagedWishList = wishListService.findAllMyWishList(pageable);

        long totalCount = pagedWishList.getTotalElements();
        int page = pagedWishList.getNumber();

        return new CustomResponseEntity<>(pagedWishList.getContent(), "내 모든 위시리스트입니다.", HttpStatus.OK, totalCount, page);
    }
}
