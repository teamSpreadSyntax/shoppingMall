package home.project.dto.responseDTO;

import home.project.domain.MemberEvent;
import home.project.domain.ProductEvent;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventResponse {

    private Long id;

    private String name;

    private Integer discountRate;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String image;

    private List<ProductEventResponse> productEventResponses;

    private List<MemberEventResponse> memberEventsResponse;

    public EventResponse(Long id, String name, Integer discountRate, String description, LocalDateTime startDate, LocalDateTime endDate, String image, List<ProductEventResponse> productEventResponses, List<MemberEventResponse> memberEventsResponse) {
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
        this.productEventResponses = productEventResponses;
        this.memberEventsResponse = memberEventsResponse;
    }
}
