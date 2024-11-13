package home.project.domain.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 상품 카테고리를 나타내는 엔티티 클래스입니다.
 * 이 클래스는 계층적 카테고리 구조를 표현하며, 자기 참조 관계를 가집니다.
 */
@Entity
@Table(name = "category")
@Getter
@Setter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Category {

    /**
     * 카테고리의 고유 식별자입니다. 자동으로 생성됩니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 카테고리의 코드입니다.
     * null이 될 수 없으며, 유니크한 값이어야 합니다.
     */
    @Column(name = "category_code", nullable = false, unique = true)
    private String code;

    /**
     * 카테고리의 이름입니다.
     * null이 될 수 없습니다.
     */
    @Column(name = "category_name", nullable = false, unique = true)
    private String name;

    /**
     * 카테고리의 계층 레벨입니다.
     * null이 될 수 없습니다.
     */
    @Column(name = "level", nullable = false)
    private Integer level;

    /**
     * 상위 카테고리에 대한 참조입니다.
     * 지연 로딩(LAZY)을 사용합니다.
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    /**
     * 하위 카테고리 목록입니다.
     * 양방향 관계로, 부모 카테고리가 삭제될 때 모든 자식 카테고리도 함께 삭제됩니다.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();


    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();
}