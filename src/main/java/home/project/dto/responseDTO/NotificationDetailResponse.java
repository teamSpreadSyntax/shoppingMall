package home.project.dto.responseDTO;

import home.project.domain.notification.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "알림 세부 응답")
public class NotificationDetailResponse {

    @Schema(description = "알림 유형", example = "ORDER_COMPLETE")
    private NotificationType notificationType;

    @Schema(description = "알림 설명", example = "주문이 완료되었습니다.")
    private String description;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    public NotificationDetailResponse(NotificationType notificationType, String description, LocalDateTime createdAt) {
        this.notificationType = notificationType;
        this.description = description;
        this.createdAt = createdAt;
    }
}
