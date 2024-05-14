package home.project.repository;

import home.project.domain.Member;
import home.project.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository  extends JpaRepository<Product, Long> {

//    @Query("SELECT p.name, p.brand, p.image FROM Product p ORDER BY p.selledcount DESC")
//    List<String> findTop5ByOrderBySelledcountDesc();
    @Query("SELECT p.brand FROM Product p ORDER BY p.brand ASC")
    List<String> findAllByOrderByBrandAsc();
    Optional<Product> findByName(String productName);

    Optional<Product> findById(Long ID);

    void deleteByName(String productName);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.name LIKE %:contents% OR p.brand LIKE %:contents% OR p.category LIKE %:category%")
    Optional<List<Product>> search(@Param("contents")String contents, @Param("category")String category);

    @Query("SELECT p FROM Product p WHERE p.category LIKE CONCAT(:category, '%')")
    Optional<List<Product>> findByCategory(@Param("category")String category);
    @Query("SELECT p FROM Product p WHERE p.brand = :brand")
    Optional<List<Product>> findByBrand(@Param("brand")String brand);
}
