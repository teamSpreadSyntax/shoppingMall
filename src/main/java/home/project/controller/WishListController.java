package home.project.controller;

import home.project.dto.requestDTO.AddWishRequestDTO;
import home.project.dto.responseDTO.ReviewResponse;
import home.project.dto.responseDTO.WishListDetailResponse;
import home.project.dto.responseDTO.WishListResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.WishListService;
import home.project.util.PageUtil;
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

import java.util.HashMap;
import java.util.Map;

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

    @Operation(summary = "WishList 등록/삭제 메서드", description = "WishList 등록/삭제 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/VerifyResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/toggle/{productId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> toggleWishList(@PathVariable Long productId) {

        WishListResponse wishListResponse = wishListService.toggleWishList(productId);

        String successMessage = "wishList 에 추가 되었습니다.";

        return new CustomResponseEntity<>(wishListResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "내 wishList 조회 메서드", description = "내 wishList 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedProductListResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/my_wishList")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findMyWishList(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "wishListId", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<WishListDetailResponse> pagedWishList = wishListService.findAllMyWishList(pageable);

        long totalCount = pagedWishList.getTotalElements();

        int page = pagedWishList.getNumber();

        String successMessage = "내 모든 wishList 입니다.";

        return new CustomResponseEntity<>(pagedWishList.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }
}