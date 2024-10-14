package home.project.dto.requestDTO;

import home.project.domain.DeliveryAddressType;
import home.project.domain.DeliveryStatusType;
import home.project.domain.DeliveryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class CreateShippingRequestDTO {

    @NotBlank(message = "배송 형태를 입력해주세요.")
    @Schema(description = "배송 형태", required = true)
    private DeliveryType deliveryType;


    @NotNull(message = "배송지를 입력해주세요.")
    @Schema(description = "배송지", required = true)
    private DeliveryAddressType deliveryAddressType;

    @NotNull(message = "새 배송지를 입력해주세요.")
    @Schema(description = "새 배송지", required = true)
    private String deliveryAddress;

}