package home.project.controller.user;

import home.project.dto.requestDTO.CreateReviewRequestDTO;
import home.project.dto.responseDTO.ReviewDetailResponse;
import home.project.dto.responseDTO.ReviewProductResponse;
import home.project.dto.responseDTO.ReviewResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.common.ReviewService;
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

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Review", description = "리뷰 관련 API입니다.")
@RequestMapping("/api/review")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;
    private final PageUtil pageUtil;

    @Operation(summary = "리뷰 작성 가능 제품 목록 조회", description = "리뷰 작성 가능한 제품 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ReviewProductResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/reviewableProducts")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getReviewableProducts(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "productId", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ReviewProductResponse> pagedProduct = reviewService.getReviewableProducts(pageable);

        long totalCount = pagedProduct.getTotalElements();
        int page = pagedProduct.getNumber();
        String successMessage = "리뷰 작성 가능 제품 목록입니다.";

        return new CustomResponseEntity<>(pagedProduct.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "리뷰 작성", description = "구매 확정된 제품에 대한 리뷰를 작성하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ReviewDetailResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "인증되지 않음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/{productOrderId}/join")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createReview(@PathVariable Long productOrderId, @RequestBody CreateReviewRequestDTO createReviewRequestDTO) {
        ReviewDetailResponse reviewDetailResponse = reviewService.join(productOrderId, createReviewRequestDTO);
        String successMessage = "리뷰가 작성되었습니다.";
        return new CustomResponseEntity<>(reviewDetailResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "내 리뷰 조회", description = "사용자의 모든 리뷰를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ReviewResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/my_review")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findMyReview(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "reviewId", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ReviewResponse> pagedReview = reviewService.findAllMyReview(pageable);

        long totalCount = pagedReview.getTotalElements();
        int page = pagedReview.getNumber();
        String successMessage = "사용자의 모든 리뷰입니다.";

        return new CustomResponseEntity<>(pagedReview.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "특정 제품의 리뷰 조회", description = "특정 제품에 대한 모든 리뷰를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ReviewDetailResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/product_review")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findProductReview(@RequestParam(value = "productId", required = false) Long productId,
                                               @PageableDefault(page = 1, size = 5)
                                               @SortDefault.SortDefaults(
                                                       {@SortDefault(sort = "reviewId", direction = Sort.Direction.ASC)})
                                               @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ReviewDetailResponse> pagedReview = reviewService.findProductReview(productId, pageable);

        long totalCount = pagedReview.getTotalElements();
        int page = pagedReview.getNumber();
        String successMessage = "해당 제품의 리뷰 목록입니다.";

        return new CustomResponseEntity<>(pagedReview.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "리뷰 좋아요 증가", description = "리뷰에 대한 좋아요 수를 증가시키는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ReviewDetailResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PatchMapping("/increase_helpful")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> increaseHelpfulCount(@RequestParam("reviewId") Long reviewId) {
        ReviewDetailResponse reviewDetailResponse = reviewService.increaseHelpfulCount(reviewId);
        String successMessage = "리뷰의 좋아요 수가 증가했습니다.";
        return new CustomResponseEntity<>(reviewDetailResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "리뷰 삭제", description = "특정 리뷰를 삭제하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("/delete")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteReview(@RequestParam("reviewId") Long reviewId) {
        reviewService.deleteById(reviewId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", reviewId + "번 리뷰가 삭제되었습니다.");
        return new CustomResponseEntity<>(responseMap, "리뷰 삭제 성공", HttpStatus.OK);
    }

    @Operation(summary = "리뷰 ID로 리뷰 조회", description = "특정 리뷰 ID를 통해 리뷰를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ReviewDetailResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/findById")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findReviewById(@RequestParam("reviewId") Long reviewId) {
        ReviewDetailResponse reviewDetailResponse = reviewService.findReviewById(reviewId);
        String successMessage = reviewId + "번 리뷰 조회 성공";
        return new CustomResponseEntity<>(reviewDetailResponse, successMessage, HttpStatus.OK);
    }
}
