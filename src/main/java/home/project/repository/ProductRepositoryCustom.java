package home.project.repository;

import home.project.domain.Category;
import home.project.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {
    Page<Product> findProducts(String brand, String category, String productName, String content, Pageable pageable);
    Page<Product> findAllByOrderByBrandAsc(Pageable pageable);
    List<Product> findAllByCategory(Category category);

}
