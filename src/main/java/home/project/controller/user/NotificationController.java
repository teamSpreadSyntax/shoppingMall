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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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


    @Operation(summary = "id로 알림 상세정보 조회 메서드", description = "id로 알림 상세 정보 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification fetched successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotificationDetailResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Notification not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))

    })
    @GetMapping("/notification_detail")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findNotificationByIdReturnQnADetailResponse(@RequestParam("notificationId") Long notificationId) {

        NotificationDetailResponse notificationDetailResponse = notificationService.findByIdReturnNotificationDetailResponse(notificationId);

        String successMessage = notificationId + "에 해당하는 알림 입니다.";

        return new CustomResponseEntity<>(notificationDetailResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "내 전체 알림 조회 메서드", description = "내 전체 알림 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications fetched successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedNotificationListResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: User not authenticated",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema")))

    })
    @GetMapping("/my_notification")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findAllMyNotification(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "notificationId", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<NotificationResponse> pagedNotification = notificationService.findAllByMemberId(pageable);

        long totalCount = pagedNotification.getTotalElements();

        int page = pagedNotification.getNumber();

        String successMessage = "내 모든 알림 입니다.";

        return new CustomResponseEntity<>(pagedNotification.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "알림 읽음 표시 메서드", description = "알림 읽음 표시 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Notification not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))


    })
    @PostMapping("/read")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> readNotification(@RequestParam("notificationId") Long notificationId) {

        String email = notificationService.readNotification(notificationId);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", email+"님의 "+notificationId + "번 알림이 읽음으로 변경되었습니다.");
        return new CustomResponseEntity<>(responseMap, "알림 읽음 상태 변경 성공", HttpStatus.OK);
    }
}
