package home.project.dto.requestDTO;

import home.project.domain.notification.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNotificationRequestDTO {

    @NotEmpty(message = "공지 제목을 입력해주세요.")
    @Schema(description = "공지 제목", required = true)
    private NotificationType notificationType;

    @NotEmpty(message = "공지 내용을 입력해주세요.")
    @Schema(description = "공지 내용", required = true)
    private String description;
}

