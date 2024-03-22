package home.project.service;

import home.project.domain.Product;

import java.util.List;
import java.util.Optional;


public interface ProductService {
    void join(Product product);
    List<Product> findAll();
    Optional<Product> findByName(String name);
    Optional<List<Product>> findByBrand(String brand);
//    Optional<Product> DetailProduct(Long productid);
    Optional<List<Product>> findByCategory(String category);
    Optional<Product> update(Product product);
    void deleteByName(String productName);
    List<String> brandList();
    Product increaseStock(Long productId, Long stock);

    Product selledCancle(Long productId, Long stock);

    Product decreaseStock(Long productId, Long stock);

    Product selledProduct(Long productId, Long stock);

}
