package home.project.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Date;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {
    @Id
    @NotNull(message = "상품ID를 입력해주세요.")
    private Long product_id;

    @Column(name = "brand")
    @NotBlank(message = "상품의 브랜드를 입력해주세요.")
    private String brand;

    @Column(name = "selledcount")
    private Long selledcount=0L;

    @Column(name = "name")
    @NotBlank(message = "상품명을 입력해주세요.")
    private String name;

    @Column(name = "image")
    @NotBlank(message = "이미지를 입력해주세요.")
    private String image;
}
