package home.project.dto;


import home.project.domain.Category2;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Category2DTOWithoutId {

    @NotEmpty(message = "코드를 입력해주세요.")
    @Column(name = "category_code")
    private String code;

    @NotEmpty(message = "이름을 입력해주세요.")
    @Column(name = "category_name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category2DTOWithoutId parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category2DTOWithoutId> children = new ArrayList<>();

    @NotNull(message = "레벨을 입력해주세요.")
    @Column(name = "level")
    private Integer level;

    public void addChild(Category2DTOWithoutId child) {
        this.children.add(child);
        child.setParent(this);
    }
}
