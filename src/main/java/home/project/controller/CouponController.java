package home.project.controller;

import home.project.domain.Coupon;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.responseDTO.CategoryResponse;
import home.project.dto.responseDTO.CouponResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.CouponService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody CreateCouponRequestDTO createCouponRequestDTO) {

        CouponResponse couponResponse = couponService.join(createCouponRequestDTO);

        String successMessage = couponResponse.getName() + "(으)로 쿠폰이 등록되었습니다.";

        return new CustomResponseEntity<>(couponResponse, successMessage, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> assignCouponToMember(@RequestBody AssignCouponToMemberRequestDTO assignCouponToMemberRequestDTO) {

        CouponResponse couponResponse = couponService.assignCouponToMember(assignCouponToMemberRequestDTO);

        String successMessage = couponResponse.getName() + "인 회원들에게 쿠폰이 부여되었습니다.";

        return new CustomResponseEntity<>(couponResponse, successMessage, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> assignCouponToProduct(@RequestBody AssignCouponToProductRequestDTO assignCouponToProductRequestDTO) {

        CouponResponse couponResponse = couponService.assignCouponToProduct(assignCouponToProductRequestDTO);

        String successMessage = couponResponse.getName() + "인 상품들에게 쿠폰이 부여되었습니다.";

        return new CustomResponseEntity<>(couponResponse, successMessage, HttpStatus.OK);
    }


}
