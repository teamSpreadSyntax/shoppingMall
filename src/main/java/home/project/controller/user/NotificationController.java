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

@Tag(name = "알림", description = "알림(Notification) 관련 API입니다.")
@RequestMapping("/api/notification")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final PageUtil pageUtil;

    @Operation(summary = "ID로 알림 상세 조회", description = "알림 ID를 통해 특정 알림의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotificationDetailResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/notification_detail")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findNotificationByIdReturnQnADetailResponse(@RequestParam("notificationId") Long notificationId) {
        NotificationDetailResponse notificationDetailResponse = notificationService.findByIdReturnNotificationDetailResponse(notificationId);
        String successMessage = notificationId + "번 알림 상세 조회 성공.";
        return new CustomResponseEntity<>(notificationDetailResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "내 전체 알림 조회", description = "로그인한 사용자의 모든 알림을 페이징 형태로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotificationResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
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
        String successMessage = "모든 알림 조회 성공.";

        return new CustomResponseEntity<>(pagedNotification.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/VerifyResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "권한 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/read")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> readNotification(@RequestParam("notificationId") Long notificationId) {
        String email = notificationService.readNotification(notificationId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", email + "님의 " + notificationId + "번 알림이 읽음으로 처리되었습니다.");
        return new CustomResponseEntity<>(responseMap, "알림 읽음 처리 성공", HttpStatus.OK);
    }
}
