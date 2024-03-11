package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface ProductService {
    void join(Product product);
    List<Product> findAll();
    Optional<Product> findByname(String name);
    Optional<Product> DetailProduct(Long productid);
    Optional<List<Product>> findByCategory(String category);
    void update(Product product);
    void deleteById(Long productid);
    List<String> brandList();
}
