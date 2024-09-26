package home.project.dto.responseDTO;

import home.project.domain.RoleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private RoleType role;

    public MemberResponseDTO(Long id, String email, String name, String phone, RoleType role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
    }
}
