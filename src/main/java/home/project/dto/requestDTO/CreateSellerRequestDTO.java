package home.project.dto.requestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSellerRequestDTO {
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
}

