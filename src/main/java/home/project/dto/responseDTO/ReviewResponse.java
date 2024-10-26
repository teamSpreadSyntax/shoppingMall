package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {

    private Long reviewId;

    private String subject;

    private String memberEmail;

    private LocalDateTime createAt;

}
