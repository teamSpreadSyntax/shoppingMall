package home.project.dto.responseDTO;

import home.project.domain.notification.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "알림 응답")
public class NotificationResponse {

    @Schema(description = "알림 ID", example = "1")
    private Long id;

    @Schema(description = "회원 ID", example = "101")
    private Long memberId;

    @Schema(description = "알림 유형", example = "ORDER_COMPLETE")
    private NotificationType notificationType;

    @Schema(description = "알림 설명", example = "주문이 완료되었습니다.")
    private String description;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    public NotificationResponse(Long id, Long memberId, NotificationType notificationType, String description, Boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.notificationType = notificationType;
        this.description = description;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
}
