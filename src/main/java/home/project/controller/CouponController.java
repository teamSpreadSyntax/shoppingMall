package home.project.controller;

import home.project.domain.Coupon;
import home.project.domain.MemberCoupon;
import home.project.domain.ProductCoupon;
import home.project.dto.requestDTO.AssignCouponToMemberRequestDTO;
import home.project.dto.requestDTO.AssignCouponToProductRequestDTO;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.responseDTO.CategoryResponse;
import home.project.dto.responseDTO.CouponResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.CouponService;
import home.project.util.PageUtil;
import home.project.util.StringBuilderUtil;
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


    @Operation(summary = "쿠폰 생성 메서드", description = "쿠폰 생성 메서드입니다.")
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
    @PostMapping("/join")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createCoupon(@RequestBody CreateCouponRequestDTO createCouponRequestDTO) {

        CouponResponse couponResponse = couponService.join(createCouponRequestDTO);

        String successMessage = couponResponse.getName() + "(으)로 쿠폰이 등록되었습니다.";

        return new CustomResponseEntity<>(couponResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "회원에게 쿠폰 부여 메서드", description = "회원에게 쿠폰 부여 메서드입니다.")
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
    @PostMapping("/assignCouponToMember")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> assignCouponToMember(@RequestBody AssignCouponToMemberRequestDTO assignCouponToMemberRequestDTO, @PageableDefault(page = 1, size = 5)
    @SortDefault.SortDefaults({
            @SortDefault(sort = "name", direction = Sort.Direction.ASC)
    }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<MemberCoupon> pagedMemberCoupon = couponService.assignCouponToMember(assignCouponToMemberRequestDTO, pageable);

        String successMessage = StringBuilderUtil.buildMemberCouponSearchCriteria(assignCouponToMemberRequestDTO.getCouponId(), assignCouponToMemberRequestDTO.getName(), assignCouponToMemberRequestDTO.getEmail(), assignCouponToMemberRequestDTO.getPhone(),
                assignCouponToMemberRequestDTO.getRole(), assignCouponToMemberRequestDTO.getContent(), pagedMemberCoupon);

        long totalCount = pagedMemberCoupon.getTotalElements();
        int page = pagedMemberCoupon.getNumber();

        return new CustomResponseEntity<>(pagedMemberCoupon.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "상품에 쿠폰 부여 메서드", description = "상품에 쿠폰 부여 메서드입니다.")
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
    @PostMapping("/assignCouponToProduct")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> assignCouponToProduct(@RequestBody AssignCouponToProductRequestDTO assignCouponToProductRequestDTO, @PageableDefault(page = 1, size = 5)
    @SortDefault.SortDefaults({
            @SortDefault(sort = "name", direction = Sort.Direction.ASC)
    }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ProductCoupon> pagedProductCoupon = couponService.assignCouponToProduct(assignCouponToProductRequestDTO, pageable);

        String successMessage = StringBuilderUtil.buildProductCouponSearchCriteria(assignCouponToProductRequestDTO.getCouponId(), assignCouponToProductRequestDTO.getBrand(), assignCouponToProductRequestDTO.getCategory(), assignCouponToProductRequestDTO.getProductName(),
                assignCouponToProductRequestDTO.getContent(), pagedProductCoupon);

        long totalCount = pagedProductCoupon.getTotalElements();
        int page = pagedProductCoupon.getNumber();

        return new CustomResponseEntity<>(pagedProductCoupon.getContent(), successMessage, HttpStatus.OK, totalCount, page);

    }


}
