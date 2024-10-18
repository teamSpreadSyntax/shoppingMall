package home.project.dto.requestDTO;

import home.project.domain.Member;
import home.project.domain.Orders;
import home.project.domain.Product;
import home.project.domain.QnAType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CreateQnARequestDTO {

    @NotBlank(message = "문의 유형을 입력해주세요.")
    @Schema(description = "문의 유형")
    private QnAType qnAType = QnAType.OTHER;

    @NotBlank(message = "제목을 입력해주세요.")
    @Schema(description = "제목")
    private String subject;

    @Schema(description = "품번")
    private String productNum = null;

    @Schema(description = "주문번호")
    private String orderNum = null;

    @NotBlank(message = "내용을 입력해주세요.")
    @Schema(description = "내용")
    private String description;

}