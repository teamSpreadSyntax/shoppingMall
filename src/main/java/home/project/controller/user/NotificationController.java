package home.project.controller.user;

import home.project.dto.responseDTO.NotificationDetailResponse;
import home.project.dto.responseDTO.NotificationResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.notification.NotificationService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification", description = "Notification 관련 API입니다")
@RequestMapping("/api/notification")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final PageUtil pageUtil;


    @Operation(summary = "전체 Notification 조회 메서드", description = "전체 Notification 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedProductListResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/all")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findAll(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "notificationId", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<NotificationResponse> pagedNotification = notificationService.findAllNotifications(pageable);

        long totalCount = pagedNotification.getTotalElements();

        int page = pagedNotification.getNumber();

        String successMessage = "모든 공지사항 입니다.";

        return new CustomResponseEntity<>(pagedNotification.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "id로 Notification 상세정보 조회 메서드", description = "id로 Notification 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/notification_detail")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findNotificationByIdReturnQnADetailResponse(@RequestParam("notificationId") Long notificationId) {

        NotificationDetailResponse notificationDetailResponse = notificationService.findByIdReturnNotificationDetailResponse(notificationId);

        String successMessage = notificationId + "에 해당하는 공지사항 입니다.";

        return new CustomResponseEntity<>(notificationDetailResponse, successMessage, HttpStatus.OK);
    }
}
