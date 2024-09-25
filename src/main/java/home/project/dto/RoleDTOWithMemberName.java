package home.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTOWithMemberName {

    private Long id;

    private String role;

    private String name;

    public RoleDTOWithMemberName(Long id, String role, String name) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}
