package home.project.repository.product;


import home.project.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    boolean existsByProductNum(String productNum);

    Product findByProductNum(String productNum);

}
