package home.project.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
//import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
//import org.springframework.security.crypto.bcrypt.BCrypt;


@Entity
@Table(name = "member", uniqueConstraints = {@UniqueConstraint(columnNames = {"phone", "email"})})
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Column(name = "email")
    private String email;

    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Column(name="password")//, columnDefinition = ("VARCHAR(255)"))
    private String password;
//    @PrePersist
//    public void prePersist(){
//        String salt = BCrypt.gensalt();
//        String hashedPassword=BCrypt.hashpw(password, salt);
//        this.password=hashedPassword;
//    }
    @NotEmpty(message = "회원 이름을 입력해주세요")
    @Column(name = "name")
    private String name;

    @NotEmpty(message = "전화번호를 입력해주세요")
    @Column(name = "phone")
    private String phone;

}
