package home.project.dto.requestDTO;


import home.project.domain.delivery.DeliveryAddressType;
import home.project.domain.delivery.DeliveryType;
import home.project.domain.delivery.ShippingMessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateShippingRequestDTO {

    @NotBlank(message = "배송 형태를 입력해주세요.")
    @Schema(description = "배송 형태", required = true)
    private DeliveryType deliveryType;


    @NotNull(message = "기존배송지를 사용할지, 새로운 배송지를 사용할지 선택해주세요")
    @Schema(description = "기존 배송지", required = true)
    private DeliveryAddressType deliveryAddressType;

    @NotNull(message = "새 배송지를 입력해주세요.")
    @Schema(description = "새 배송지", required = true)
    private String deliveryAddress;

    @NotNull(message = "배송메세지를 선택해주세요.")
    @Schema(description = "배송메세지", required = true)
    private ShippingMessageType shippingMessages;

    @Schema(description = "커스텀메세지")
    private String customMessage = null;
}