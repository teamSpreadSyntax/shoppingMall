package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    void join(Product product);
    Optional<Product> findByname(String name);
    List<Product> findAll();
    void update(Product product);
    void deleteById(Product product);
    List<String> brandList();
}
