package home.project.controller.user;

import home.project.dto.requestDTO.ProductDTOForOrder;
import home.project.dto.responseDTO.CartResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.order.CartService;
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

@Tag(name = "장바구니", description = "장바구니 관련 API입니다.")
@RequestMapping("/api/cart")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class CartController {

    private final CartService cartService;
    private final ShippingService shippingService;
    private final PageUtil pageUtil;

    @Operation(summary = "장바구니에 상품 추가", description = "장바구니에 상품을 추가하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니 추가 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/CartResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/add")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createCart(@RequestParam(value = "productId", required = false) Long productId,
                                        @RequestParam(value = "quantity", required = false) Integer quantity) {

        CartResponse cartResponse = cartService.join(productId, quantity);
        String successMessage = "상품이 장바구니에 추가되었습니다.";
        return new CustomResponseEntity<>(cartResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "장바구니 조회", description = "사용자의 장바구니 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니 조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedProductListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/list")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findByEmail(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "cartId", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<ProductDTOForOrder> pagedCart = cartService.findAllByMemberId(pageable);

        long totalCount = pagedCart.getTotalElements();
        int page = pagedCart.getNumber();
        String successMessage = "장바구니에 담긴 상품들입니다.";

        return new CustomResponseEntity<>(pagedCart.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 특정 상품을 삭제하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니 삭제 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("/remove")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteFromCart(@RequestParam("cartId") Long cartId) {
        String name = cartService.deleteById(cartId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", name + "(id:" + cartId + ")(이)가 장바구니에서 삭제되었습니다.");
        return new CustomResponseEntity<>(responseMap, "상품 삭제 성공", HttpStatus.OK);
    }
}
