package home.project.repository;

import home.project.domain.Member;
import home.project.domain.Product;
import home.project.domain.ProductDTOWithBrandId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository  extends JpaRepository<Product, Long> {

//    @Query("SELECT p.name, p.brand, p.image FROM Product p ORDER BY p.selledcount DESC")
//    List<String> findTop5ByOrderBySelledcountDesc();
    @Query("SELECT DISTINCT p.brand FROM Product p ORDER BY p.brand ASC")
    Page<Product> findAllByOrderByBrandAsc(Pageable pageable);

    Optional<Product> findByName(String productName);

    Optional<Product> findById(Long ID);

    void deleteByName(String productName);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.name LIKE %:contents% OR p.brand LIKE %:contents% OR p.category LIKE %:category%")
    Page<Product> search(@Param("contents")String contents, @Param("category")String category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category LIKE CONCAT(:category, '%')")
    Page<Product> findByCategory(@Param("category")String category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand")
    Page<Product> findByBrand(@Param("brand")String brand, Pageable pageable);

    Page<Product> findByNameContaining(String name, Pageable pageable);
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
