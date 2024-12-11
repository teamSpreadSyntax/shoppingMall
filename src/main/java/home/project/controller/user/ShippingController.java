package home.project.controller.user;

import home.project.dto.responseDTO.ShippingResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.order.ShippingService;
import home.project.service.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "배송", description = "배송 관련 API입니다.")
@RequestMapping("/api/shipping")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class ShippingController {

    private final ShippingService shippingService;

    @Operation(summary = "ID로 배송 조회", description = "배송 ID를 통해 특정 배송 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ShippingResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/shipping")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findShippingById(@RequestParam("shippingId") Long shippingId) {
        ShippingResponse shippingResponse = shippingService.findByIdReturnShippingResponse(shippingId);
        String successMessage = shippingId + "번 배송 정보를 조회했습니다.";
        return new CustomResponseEntity<>(shippingResponse, successMessage, HttpStatus.OK);
    }
}
