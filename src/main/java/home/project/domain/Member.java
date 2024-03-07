package home.project.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
//import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
//import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Date;


@Entity
@Table(name = "member",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"phone", "email"})})
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Column(name = "email", nullable = false)
    private String email;

    @NotEmpty
    @Column(name="password", nullable = false)//, columnDefinition = ("VARCHAR(255)"))
    private String password;
//    @PrePersist
//    public void prePersist(){
//        String salt = BCrypt.gensalt();
//        String hashedPassword=BCrypt.hashpw(password, salt);
//        this.password=hashedPassword;
//    }
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sex")
    private String sex="";

    @NotNull
    @Column(name = "birth", nullable = false)
    private Date birth;

    @Column(name = "age")
    private Integer age=0;

    @Column(name = "address_num")
    private String address_num="";

    @Column(name = "address")
    private String address="";

    @Column(name = "address_detail")
    private String address_detail="";

    @Column(name = "address_name")
    private String address_name="";

    @NotEmpty
    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "mileage")
    private Integer mileage=0;

    @Column(name = "coupon")
    private String coupon="";

    @Column(name = "total")
    private String total="";

}
