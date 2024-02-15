package home.project.repository;

import home.project.domain.Member;
import home.project.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository  extends JpaRepository<Product, Long> {

    public List<Product> findTop5ByOrderBySelledcountDesc();
    @Query("SELECT p.brand FROM Product p ORDER BY p.brand ASC")
    public List<String> findAllByOrderByBrandAsc();
    public Optional<Product> findByName(Optional<String> product);
    Optional<Object> findByBrand(Optional<String> brand);
    Optional<Object> findByImage(Optional<String> image);
}
