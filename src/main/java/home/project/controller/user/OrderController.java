package home.project.controller.user;
//123
import home.project.domain.delivery.DeliveryStatusType;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.responseDTO.OrderResponse;
import home.project.dto.responseDTO.ShippingResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.order.OrderService;
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

@Tag(name = "주문", description = "주문관련 API입니다")
@RequestMapping("/api/order")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;
    private final ShippingService shippingService;
    private final PageUtil pageUtil;


    @Operation(summary = "주문 생성 메서드", description = "주문 생성 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/OrderResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))


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
            @ApiResponse(responseCode = "200", description = "Order fetched successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/OrderResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/order")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findOrderById(@RequestParam("orderId") Long orderId) {
        OrderResponse orderResponse = orderService.findByIdReturnOrderResponse(orderId);
        String successMessage = orderId + "에 해당하는 주문 입니다.";
        return new CustomResponseEntity<>(orderResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "내 주문 조회 메서드", description = "내 주문 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders fetched successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedOrderResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Orders not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/my_order")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findOrderByMemberId(
        @PageableDefault(page = 1, size = 5)
        @SortDefault.SortDefaults({
                @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<OrderResponse> pagedOrderResponse = orderService.findByMemberId(pageable);

            long totalCount = pagedOrderResponse.getTotalElements();

            int page = pagedOrderResponse.getNumber();

        String successMessage = "내 주문목록 입니다.";
        return new CustomResponseEntity<>(pagedOrderResponse.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "주문 취소 메서드", description = "주문 취소 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order canceled successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("/cancel")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> cancelOrder(@RequestParam("orderId") Long orderId) {
        String name = orderService.deleteById(orderId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", name + "(id:" + orderId + ")(이)이 취소되었습니다.");
        return new CustomResponseEntity<>(responseMap, "주문 취소 성공", HttpStatus.OK);
    }


    @Operation(summary = "반품 신청 메서드", description = "반품 신청 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refund request successful",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ShippingResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/refund")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> refundRequest(@RequestParam("shippingId") Long shippingId) {

        DeliveryStatusType deliveryStatusType = DeliveryStatusType.REFUND_REQUESTED;

        ShippingResponse shippingResponse = shippingService.update(shippingId, deliveryStatusType);

        String successMessage = shippingResponse.getDeliveryNum() + "의 반품 신청이 완료되었습니다.";

        return new CustomResponseEntity<>(shippingResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "교환 신청 메서드", description = "교환 신청 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exchange request successful",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ShippingResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/change")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> changeRequest(@RequestParam("shippingId") Long shippingId) {

        DeliveryStatusType deliveryStatusType = DeliveryStatusType.CHANGE_REQUESTED;

        ShippingResponse shippingResponse = shippingService.update(shippingId, deliveryStatusType);

        String successMessage = shippingResponse.getDeliveryNum() + "의 교환 신청이 완료되었습니다.";

        return new CustomResponseEntity<>(shippingResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "구매 확정 메서드", description = "구매 확정 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase confirmed successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @PostMapping("/confirm")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> confirmPurchase(@RequestParam("orderId") Long orderId) {

        orderService.confirmPurchase(orderId);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", "주문 ID: " + orderId + "에 대한 구매가 확정되었습니다.");

        return new CustomResponseEntity<>(responseMap, "구매 확정", HttpStatus.OK);
    }




}
