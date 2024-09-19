//package home.project.domain;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.NotNull;
//import jdk.jfr.Name;
//import lombok.Getter;
//import lombok.Setter;
//
//
//@Entity
//@Table(name = "category", uniqueConstraints = {@UniqueConstraint(columnNames = {"category_code", "category_name"})})
//
//@Getter
//@Setter
//public class Category {
//    @Id
//    @Column(name = "category_id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @NotEmpty(message = "코드를 입력해주세요.")
//    @Column(name = "category_code")
//    private String code;
//
//    @NotEmpty(message = "이름을 입력해주세요.")
//    @Column(name = "category_name")
//    private String name;
//
//    @Column(name = "parent_code")
//    private String parentCode;
//
//    @NotNull(message = "레벨을 입력해주세요.")
//    @Column(name = "level")
//    private Integer level;
//}
