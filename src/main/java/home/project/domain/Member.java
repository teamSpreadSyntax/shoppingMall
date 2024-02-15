package home.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Entity
@Table(name = "member",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"phone"})})
@Getter
@Setter
public class Member {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "sex")
    private String sex="";

    @Column(name = "birth")
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

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "mileage")
    private Integer mileage=0;

    @Column(name = "coupon")
    private String coupon="";

    @Column(name = "total")
    private String total="";

}
