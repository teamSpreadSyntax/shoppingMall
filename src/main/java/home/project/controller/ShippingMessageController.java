package home.project.controller;

import home.project.domain.ShippingMessage;
import home.project.dto.requestDTO.CreateShippingMessageRequestDTO;
import home.project.dto.responseDTO.ShippingMessageResponse;
import home.project.dto.responseDTO.ShippingResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.ShippingMessageService;
import home.project.util.PageUtil;
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

@Tag(name = "배송 메시지", description = "배송 메시지 관련 API입니다")
@RequestMapping("/api/shipping-messages")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class ShippingMessageController {

    private final ShippingMessageService shippingMessageService;
    private final PageUtil pageUtil;

    @Operation(summary = "배송 메시지 생성", description = "새로운 배송 메시지를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ShippingMessageResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @PostMapping("/create")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createShippingMessage(@RequestBody CreateShippingMessageRequestDTO requestDTO) {
        ShippingMessageResponse response = shippingMessageService.createShippingMessage(requestDTO);
        String successMessage = "배송 메시지가 생성되었습니다.";
        return new CustomResponseEntity<>(response, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "배송 메시지 조회", description = "특정 ID의 배송 메시지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ShippingMessageResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/detail")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getShippingMessageById(@RequestParam("id") Long id) {
        ShippingMessage shippingMessage = shippingMessageService.findById(id);
        String successMessage = id + "번 배송 메시지입니다.";
        return new CustomResponseEntity<>(shippingMessage.getContent(), successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체 배송 메시지 조회", description = "전체 배송 메시지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedShippingMessageListResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getAllShippingMessages(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({@SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);

        Page<ShippingMessageResponse> pagedMessages = shippingMessageService.findAll(pageable);
        long totalCount = pagedMessages.getTotalElements();
        int page = pagedMessages.getNumber();
        String successMessage = "전체 배송 메시지입니다.";
        return new CustomResponseEntity<>(pagedMessages.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "배송 메시지 수정", description = "기존의 배송 메시지를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ShippingMessageResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PutMapping("/update")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateShippingMessage(@RequestParam("id") Long id, @RequestBody String updatedMessage) {
        ShippingMessageResponse response = shippingMessageService.updateShippingMessage(id, updatedMessage);
        String successMessage = id + "번 배송 메시지가 수정되었습니다.";
        return new CustomResponseEntity<>(response, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "배송 메시지 삭제", description = "특정 배송 메시지를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("/delete")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteShippingMessage(@RequestParam("id") Long id) {
        shippingMessageService.deleteShippingMessage(id);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", id + "번 배송 메시지가 삭제되었습니다.");
        return new CustomResponseEntity<>(responseMap, "배송 메시지 삭제 성공", HttpStatus.OK);
    }
}
