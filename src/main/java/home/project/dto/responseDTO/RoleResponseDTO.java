package home.project.dto.responseDTO;

import home.project.domain.RoleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponseDTO {

    private Long id;

    private RoleType role;

    private String name;

    public RoleResponseDTO(Long id, RoleType role, String name) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}
