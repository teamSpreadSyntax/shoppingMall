package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "이벤트 응답")
public class EventResponse {

    @Schema(description = "이벤트 ID", example = "1")
    private Long id;

    @Schema(description = "이벤트 이름", example = "봄맞이 할인 이벤트")
    private String name;

    @Schema(description = "이벤트 설명", example = "봄 시즌 맞이 50% 할인")
    private List<String> description;

    @Schema(description = "이벤트 시작 날짜")
    private LocalDateTime startDate;

    @Schema(description = "이벤트 종료 날짜")
    private LocalDateTime endDate;

    public EventResponse(Long id, String name, List<String> description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
