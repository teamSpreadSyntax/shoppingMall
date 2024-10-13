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
public class CreateOrderRequestDTO {

    @NotBlank(message = "상품번호를 입력해주세요.")
    @Schema(description = "상품번호", required = true)
    private String productNumber;

    @NotNull(message = "수량을 입력해주세요.")
    @Schema(description = "주문 수량", required = true)
    private Integer quantity;

}