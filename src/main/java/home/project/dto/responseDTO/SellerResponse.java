package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "판매자 응답")
public class SellerResponse {

    @Schema(description = "판매자 ID", example = "1")
    private Long id;

    @Schema(description = "판매자 이름", example = "리바이스")
    private String name;

    @Schema(description = "판매자 전화번호", example = "01012345678")
    private String phoneNumber;

    @Schema(description = "판매자 이메일", example = "seller@example.com")
    private String email;

    @Schema(description = "판매자 주소", example = "서울특별시 강남구 테헤란로")
    private String address;

    public SellerResponse(Long id, String name, String phoneNumber, String email, String address) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }
}
