package home.project.repository;

import home.project.domain.Member;
import home.project.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository  extends JpaRepository<Product, Long> {

    public List<Product> findTop5BySelledcountOrderBySelledcountDesc(Product product);
    public Optional<Product> findByName(Optional<String> product);
}
