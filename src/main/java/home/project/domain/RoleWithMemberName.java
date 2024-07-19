package home.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleWithMemberName {

    private Long id;

    private String role;

    private String name;

    public RoleWithMemberName(Long id, String role, String name){
        this.id = id;
        this.name = name;
        this.role = role;
    }
}
