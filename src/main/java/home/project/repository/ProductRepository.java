package home.project.repository;

import home.project.domain.Member;
import home.project.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository  extends JpaRepository<Product, Long> {

//    @Query("SELECT p.name, p.brand, p.image FROM Product p ORDER BY p.selledcount DESC")
//    List<String> findTop5ByOrderBySelledcountDesc();
    @Query("SELECT p.brand FROM Product p ORDER BY p.brand ASC")
    List<String> findAllByOrderByBrandAsc();
    Optional<Product> findByname(String productname);
}
