package home.project.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Name;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "category", uniqueConstraints = {@UniqueConstraint(columnNames = {"category_code", "category_name"})})

@Getter
@Setter
public class Category {
    @Id
    @Column(name = "category_code")
    private String code;

    @Column(name = "category_name", nullable = false)
    private String name;

    @Column(name = "parent_code")
    private String parentCode;

    @Column(name = "level", nullable = false)
    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code", referencedColumnName = "category_code", insertable = false, updatable = false)
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();
}
