package home.project.repository;


import home.project.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ProductRepository  extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p.brand FROM Product p ORDER BY p.brand ASC")
    Page<Product> findAllByOrderByBrandAsc(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "(:brand IS NULL OR :brand = '' OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:category IS NULL OR :category = '' OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%'))) AND " +
            "(:productName IS NULL OR :productName = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
            "(:content IS NULL OR :content = '' OR " +
            "(LOWER(p.brand) LIKE LOWER(CONCAT('%', :content, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :content, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :content, '%'))))")
    Page<Product> findProducts(@Param("brand") String brand,
                               @Param("category") String category,
                               @Param("productName") String productName,
                               @Param("content") String query,
                               Pageable pageable);
}
