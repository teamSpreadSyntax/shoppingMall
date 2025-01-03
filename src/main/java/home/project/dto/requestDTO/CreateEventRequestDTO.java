package home.project.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class CreateEventRequestDTO {

    @NotBlank(message = "이벤트 이름을 입력해주세요.")
    @Schema(description = "이벤트 이름", required = true)
    private String name;

    @NotNull(message = "이벤트 시작날짜를 입력해주세요.")
    @Schema(description = "이벤트 시작 날짜", required = true)
    private LocalDateTime startDate;

    @NotNull(message = "이벤트 종료날짜를 입력해주세요.")
    @Schema(description = "이벤트 종료 날짜", required = true)
    private LocalDateTime endDate;

    @NotNull(message = "쿠폰 id를 입력해주세요.")
    @Schema(description = "쿠폰 id", required = true)
    private Long couponId;

}