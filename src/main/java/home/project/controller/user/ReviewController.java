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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Tag(name = "Review", description = "Review관련 API입니다")
    @RequestMapping("/api/review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
    })
    @RequiredArgsConstructor
    @RestController
public class ReviewController {

    private final ReviewService reviewService;
    private final PageUtil pageUtil;

    @Operation(summary = "리뷰 작성 가능 제품 목록 조회 메서드", description = "리뷰 작성 가능 제품 목록 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched reviewable products",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedProductListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad request",
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

        String successMessage = "리뷰 작성 가능 제품 목록 입니다.";

        return new CustomResponseEntity<>(pagedProduct.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "Review 작성 메서드", description = "구매 확정된 제품에 대한 Review를 작성하는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created the review",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ReviewDetailResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema")))

    })
    @PostMapping(value = "/join", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createReview(
            @RequestParam("productId") Long productId,
            @RequestPart("reviewData") @Valid CreateReviewRequestDTO createReviewRequestDTO,
            @RequestPart(value = "descriptionImages", required = false) MultipartFile[] descriptionImages) {

        List<MultipartFile> imageList = descriptionImages != null ?
                Arrays.asList(descriptionImages) : new ArrayList<>();

        ReviewDetailResponse reviewDetailResponse = reviewService.join(productId, createReviewRequestDTO , imageList);

        String successMessage = "리뷰가 작성되었습니다.";

        return new CustomResponseEntity<>(reviewDetailResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "내 Review 조회 메서드", description = "내 Review 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched user reviews",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedReviewResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema")))

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

        String successMessage = "내 모든 Review 입니다.";

        return new CustomResponseEntity<>(pagedReview.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "상품에 해당하는 Review 조회 메서드", description = "상품에 해당하는 Review 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched product reviews",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedReviewDetailResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad request",
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

        String successMessage = "상품에 해당하는 모든 Review 입니다.";

        return new CustomResponseEntity<>(pagedReview.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "리뷰 좋아요 증가", description = "리뷰의 좋아요 수를 증가시킵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully increased helpful count",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ReviewDetailResponseSchema")))

    })
    @PatchMapping("/increase_helpful")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> increaseHelpfulCount(@RequestParam("reviewId") Long reviewId) {

        ReviewDetailResponse reviewDetailResponse = reviewService.increaseHelpfulCount(reviewId);

        String successMessage = "리뷰의 좋아요 수가 증가했습니다.";

        return new CustomResponseEntity<>(reviewDetailResponse, successMessage, HttpStatus.OK);
    }
    @Operation(summary = "리뷰 삭제 메서드", description = "리뷰 삭제 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted review",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @DeleteMapping("delete")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteReview(@RequestParam("reviewId") Long reviewId) {
        reviewService.deleteById(reviewId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", reviewId + "번 리뷰가 삭제되었습니다.");
        return new CustomResponseEntity<>(responseMap, "리뷰 삭제 성공", HttpStatus.OK);
    }

    @Operation(summary = "리뷰 ID로 리뷰 조회", description = "리뷰 ID를 통해 특정 리뷰를 조회하는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched review details",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ReviewDetailResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/review")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findReviewById(@RequestParam("reviewId") Long reviewId) {
        ReviewDetailResponse reviewDetailResponse = reviewService.findReviewById(reviewId);

        String successMessage = reviewId + "번 리뷰 조회 성공";

        return new CustomResponseEntity<>(reviewDetailResponse, successMessage, HttpStatus.OK);
    }


}
