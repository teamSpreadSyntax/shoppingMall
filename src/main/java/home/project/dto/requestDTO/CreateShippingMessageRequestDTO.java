package home.project.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateShippingMessageRequestDTO {

    @NotEmpty(message = "배송 메시지를 입력해주세요.")
    @Schema(description = "배송 메시지 리스트", required = true)
    private List<String> messages;
}
