package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;

    public SellerResponse(Long id, String name, String phoneNumber, String email, String address) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }
}
