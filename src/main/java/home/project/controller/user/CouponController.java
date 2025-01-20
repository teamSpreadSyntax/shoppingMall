package home.project.controller.user;

import home.project.dto.responseDTO.CouponResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.promotion.CouponService;
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

@Tag(name = "쿠폰", description = "쿠폰관련 API입니다")
@RequestMapping("/api/coupon")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class CouponController {

    private final CouponService couponService;
    private final PageUtil pageUtil;

    @Operation(summary = "id로 쿠폰 조회 메서드", description = "id로 쿠폰 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupons retrieved successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedCouponListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/coupon")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findCouponById(@RequestParam("couponId") Long couponId) {
        CouponResponse couponResponse = couponService.findByIdReturnCouponResponse(couponId);
        String successMessage = couponId + "에 해당하는 쿠폰 입니다.";
        return new CustomResponseEntity<>(couponResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "회원 id로 쿠폰 조회 메서드", description = "회원 id로 쿠폰 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupons retrieved successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedCouponListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))

    })
    @GetMapping("/mycoupon")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findAllCouponByMemberId(
            @PageableDefault(page = 1, size = 5)
    @SortDefault.SortDefaults(
            {@SortDefault(sort = "startDate", direction = Sort.Direction.ASC)})
    @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<CouponResponse> pagedCouponResponse = couponService.findAllByMemberIdReturnCouponResponse(pageable);
        long totalCount = pagedCouponResponse.getTotalElements();

        int page = pagedCouponResponse.getNumber();

        String successMessage = "내 전체 쿠폰입니다.";

        return new CustomResponseEntity<>(pagedCouponResponse.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "전체 쿠폰 조회 메서드", description = "전체 쿠폰 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Best coupon retrieved successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/CouponResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Coupon not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/coupons")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findAll(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "startDate", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<CouponResponse> pagedCoupon = couponService.findAll(pageable);

        long totalCount = pagedCoupon.getTotalElements();

        int page = pagedCoupon.getNumber();

        String successMessage = "전체 쿠폰입니다.";

        return new CustomResponseEntity<>(pagedCoupon.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "쿠폰 통합 조회 메서드", description = "쿠폰이름, 사용시작날짜, 사용종료날짜, 쿠폰부여조건 및 일반 검색어로 쿠폰을 조회합니다. 모든 조건을 만족하는 쿠폰을 조회합니다. 검색어가 없으면 전체 쿠폰을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupons retrieved successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedCouponListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))

    })
    @GetMapping("/search")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> searchCoupon(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "assignBy", required = false) String assignBy,
            @RequestParam(value = "content", required = false) String content,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);

        Page<CouponResponse> pagedCouponResponse = couponService.findCoupons(name, startDate, endDate, assignBy, content, pageable);

        String successMessage = StringBuilderUtil.buildCouponSearchCriteria(name, startDate, endDate, assignBy, content, pagedCouponResponse);

        long totalCount = pagedCouponResponse.getTotalElements();
        int page = pagedCouponResponse.getNumber();

        return new CustomResponseEntity<>(pagedCouponResponse.getContent(), successMessage, HttpStatus.OK, totalCount, page);

    }

    @Operation(summary = "최적 쿠폰 조회 메서드", description = "특정 회원과 상품에 대해 가장 적절한 쿠폰을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Best coupon retrieved successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/CouponResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Coupon not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/best-coupon")
    @SecurityRequirement(name = "bearerAuth")
    public CustomResponseEntity<?> getBestCoupon(
            @RequestParam("productId") Long productId) {

        CouponResponse bestCouponResponse = couponService.selectBestCouponForMember(productId);

        return new CustomResponseEntity<>(bestCouponResponse, "가장 적절한 쿠폰 조회 성공", HttpStatus.OK);
    }

}
