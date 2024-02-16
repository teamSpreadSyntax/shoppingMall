package home.project.domain;

import jakarta.persistence.*;
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
    private Long product_id;

    @Column(name = "brand")
    private String brand;

    @Column(name = "selledcount")
    private Long selledcount=0L;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;
}
