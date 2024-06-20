package home.project.service;

import home.project.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;


public interface ProductService {

    void join(Product product);

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(Long ID);

    Optional<Product> update(Product product);

    void deleteById(Long productId);

    Page<Product> findProducts(String brand, String category, String productName, String query, Pageable pageable);

    Page<Product> brandList(Pageable pageable);

    Product increaseStock(Long productId, Long stock);

    Product decreaseStock(Long productId, Long stock);

}
