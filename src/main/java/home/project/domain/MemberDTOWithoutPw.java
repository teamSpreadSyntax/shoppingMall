package home.project.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTOWithoutPw {
    private Long id;
    private String email;
    private String name;
    private String phone;

    public MemberDTOWithoutPw(Long id, String email, String name, String phone) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }
}
