package home.project.controller.admin;

import home.project.dto.requestDTO.CreateNotificationRequestDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "AdminNotification", description = "AdminNotification 관련 API입니다")
@RequestMapping("/api/admin/notification")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class AdminNotificationController {

    private final NotificationService notificationService;
    private final PageUtil pageUtil;


    @Operation(summary = "Notification 작성 메서드", description = "Notification 작성 메서드입니다.")
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
    public ResponseEntity<?> createNotification(@RequestBody CreateNotificationRequestDTO createNotificationRequestDTO) {

        NotificationResponse notificationResponse = notificationService.createNotification(createNotificationRequestDTO);

        String successMessage = "공지사항이 작성되었습니다.";

        return new CustomResponseEntity<>(notificationResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "Notification 삭제 메서드", description = "Notification 삭제 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("delete")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteQnA(@RequestParam("notificationId") Long notificationId) {
        notificationService.deleteById(notificationId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", notificationId + "번 공지사항이 삭제되었습니다.");
        return new CustomResponseEntity<>(responseMap, "공지사항 삭제 성공", HttpStatus.OK);
    }
}
