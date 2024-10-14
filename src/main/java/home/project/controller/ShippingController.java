package home.project.controller;

import home.project.domain.DeliveryStatusType;
import home.project.dto.requestDTO.CreateShippingRequestDTO;
import home.project.dto.responseDTO.ShippingResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.ShippingService;
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

import java.util.HashMap;
import java.util.Map;

@Tag(name = "주문", description = "주문관련 API입니다")
@RequestMapping("/api/shipping")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class ShippingController {

    private final ShippingService shippingService;
    private final PageUtil pageUtil;


    @Operation(summary = "배송 생성 메서드", description = "배송 생성 메서드입니다.")
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
    public ResponseEntity<?> updateShipping(@RequestBody Long shippingId, DeliveryStatusType deliveryStatusType) {

        ShippingResponse shippingResponse = shippingService.update(shippingId, deliveryStatusType);

        String successMessage = shippingResponse.getDeliveryNum() + "(으)로 배송장이 등록되었습니다.";

        return new CustomResponseEntity<>(shippingResponse, successMessage, HttpStatus.OK);
    }

//    @Operation(summary = "id로 배송 조회 메서드", description = "id로 배송 조회 메서드입니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successful operation",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseSchema"))),
//            @ApiResponse(responseCode = "404", description = "Resource not found",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
//    })
//    @GetMapping("/shipping")
//    @SecurityRequirement(name = "bearerAuth")
//    public ResponseEntity<?> findShippingById(@RequestParam("shippingId") Long shippingId) {
//        ShippingResponse shippingResponse = shippingService.findByIdReturnShippingResponse(shippingId);
//        String successMessage = shippingId + "에 해당하는 배송 입니다.";
//        return new CustomResponseEntity<>(shippingResponse, successMessage, HttpStatus.OK);
//    }
//
//    @Operation(summary = "관리자를 위한 전체 배송 조회 메서드", description = "관리자를 위한 전체 배송 조회 메서드입니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successful operation",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedProductListResponseSchema"))),
//            @ApiResponse(responseCode = "404", description = "Resource not found",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
//            @ApiResponse(responseCode = "400", description = "Bad Request",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
//    })
//    @GetMapping("/admin/shippings")
//    @SecurityRequirement(name = "bearerAuth")
//    public ResponseEntity<?> findAll(
//            @PageableDefault(page = 1, size = 5)
//            @SortDefault.SortDefaults(
//                    {@SortDefault(sort = "startDate", direction = Sort.Direction.ASC)})
//            @ParameterObject Pageable pageable) {
//        pageable = pageUtil.pageable(pageable);
//        Page<ShippingResponse> pagedShipping = shippingService.findAll(pageable);
//
//        long totalCount = pagedShipping.getTotalElements();
//
//        int page = pagedShipping.getNumber();
//
//        String successMessage = "전체 배송입니다.";
//
//        return new CustomResponseEntity<>(pagedShipping.getContent(), successMessage, HttpStatus.OK, totalCount, page);
//    }
//
//    @Operation(summary = "배송 통합 조회 메서드", description = "배송번호, 배송날짜, 상품 품번, 회원 이메일 및 일반 검색어로 쿠폰을 조회합니다. 모든 조건을 만족하는 쿠폰을 조회합니다. 검색어가 없으면 전체 쿠폰을 조회합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successful operation",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedProductListResponseSchema"))),
//            @ApiResponse(responseCode = "400", description = "Bad Request",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
//            @ApiResponse(responseCode = "404", description = "Resource not found",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
//    })
//    @GetMapping("/search")
//    @SecurityRequirement(name = "bearerAuth")
//    public ResponseEntity<?> searchShipping(
//            @RequestParam(value = "shippingNum", required = false) String shippingNum,
//            @RequestParam(value = "shippingDate", required = false) String shippingDate,
//            @RequestParam(value = "deliveryAddress", required = false) String productNumber,
//            @RequestParam(value = "memberEmail", required = false) String email,
//            @RequestParam(value = "content", required = false) String content,
//            @PageableDefault(page = 1, size = 5)
//            @SortDefault.SortDefaults({
//                    @SortDefault(sort = "shippings_num", direction = Sort.Direction.ASC)
//            }) @ParameterObject Pageable pageable) {
//        pageable = pageUtil.pageable(pageable);
//
//        Page<ShippingResponse> pagedShippingResponse = shippingService.findShippings(shippingNum, shippingDate, productNumber, email, content, pageable);
//
//        String successMessage = StringBuilderUtil.buildShippingSearchCriteria(shippingNum, shippingDate, productNumber, email, content, pagedShippingResponse);
//
//        long totalCount = pagedShippingResponse.getTotalElements();
//        int page = pagedShippingResponse.getNumber();
//
//        return new CustomResponseEntity<>(pagedShippingResponse.getContent(), successMessage, HttpStatus.OK, totalCount, page);
//
//    }
//
//    @Operation(summary = "관리자를 위한 배송 취소 메서드", description = "배송 취소 메서드입니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successful operation",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
//            @ApiResponse(responseCode = "403", description = "Forbidden",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
//            @ApiResponse(responseCode = "404", description = "Resource not found",
//                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
//    })
//    @DeleteMapping("/admin/cancel")
//    @SecurityRequirement(name = "bearerAuth")
//    public ResponseEntity<?> cancelShipping(@RequestParam("shippingId") Long shippingId) {
//        String name = shippingService.deleteById(shippingId);
//        Map<String, String> responseMap = new HashMap<>();
//        responseMap.put("successMessage", name + "(id:" + shippingId + ")(이)가 삭제되었습니다.");
//        return new CustomResponseEntity<>(responseMap, "배송 취소 성공", HttpStatus.OK);
//    }


}
