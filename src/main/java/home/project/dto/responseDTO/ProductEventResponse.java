package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ProductEventResponse {

    private Long id;

    private String productNum;

    private Long eventId;

    private LocalDateTime createdAt;

    public ProductEventResponse(Long id, String productNum, Long eventId, LocalDateTime createdAt) {
        this.id = id;
        this.productNum = productNum;
        this.eventId = eventId;
        this.createdAt = createdAt;
    }
}