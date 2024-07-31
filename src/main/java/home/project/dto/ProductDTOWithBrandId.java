package home.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTOWithBrandId {

    private String brand;
    private long id;

    public ProductDTOWithBrandId(String brand, Long id) {
        this.brand = brand;
        this.id = id;
    }

}
