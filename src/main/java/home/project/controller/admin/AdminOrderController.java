package home.project.controller.admin;

import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.responseDTO.OrderResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.order.OrderService;
import home.project.service.order.ShippingService;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "관리자 주문", description = "관리자를 위한 주문 관련 API입니다")
@RequestMapping("/api/admin/order")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class AdminOrderController {

    private final OrderService orderService;
    private final ShippingService shippingService;
    private final PageUtil pageUtil;


    @Operation(summary = "주문 생성 메서드", description = "주문 생성 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/OrderResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @PostMapping("/join")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequestDTO createOrderRequestDTO) {

        OrderResponse orderResponse = orderService.join(createOrderRequestDTO);

        String successMessage = orderResponse.getOrderNum() + "(으)로 주문이 등록되었습니다.";

        return new CustomResponseEntity<>(orderResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "id로 주문 조회 메서드", description = "id로 주문 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/OrderResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/order")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findOrderById(@RequestParam("orderId") Long orderId) {
        OrderResponse orderResponse = orderService.findByIdReturnOrderResponse(orderId);
        String successMessage = orderId + "에 해당하는 주문 입니다.";
        return new CustomResponseEntity<>(orderResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체 주문 조회 메서드", description = "전체 주문 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedOrderListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/orders")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findAll(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "startDate", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<OrderResponse> pagedOrder = orderService.findAll(pageable);

        long totalCount = pagedOrder.getTotalElements();

        int page = pagedOrder.getNumber();

        String successMessage = "전체 주문입니다.";

        return new CustomResponseEntity<>(pagedOrder.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "주문 통합 조회 메서드", description = "주문번호, 주문날짜, 상품 품번, 회원 이메일 및 일반 검색어로 쿠폰을 조회합니다. 모든 조건을 만족하는 쿠폰을 조회합니다. 검색어가 없으면 전체 쿠폰을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedOrderListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/search")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> searchOrder(
            @RequestParam(value = "orderNum", required = false) String orderNum,
            @RequestParam(value = "orderDate", required = false) String orderDate,
            @RequestParam(value = "deliveryAddress", required = false) String productNumber,
            @RequestParam(value = "memberEmail", required = false) String email,
            @RequestParam(value = "content", required = false) String content,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "orders_num", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);

        Page<OrderResponse> pagedOrderResponse = orderService.findOrders(orderNum, orderDate, productNumber, email, content, pageable);

        String successMessage = StringBuilderUtil.buildOrderSearchCriteria(orderNum, orderDate, productNumber, email, content, pagedOrderResponse);

        long totalCount = pagedOrderResponse.getTotalElements();
        int page = pagedOrderResponse.getNumber();

        return new CustomResponseEntity<>(pagedOrderResponse.getContent(), successMessage, HttpStatus.OK, totalCount, page);

    }

}
