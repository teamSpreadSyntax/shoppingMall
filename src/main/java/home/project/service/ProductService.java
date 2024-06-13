package home.project.service;

import home.project.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface ProductService {
    void join(Product product);
    Page<Product> findAll(Pageable pageable);
    Page<Product> findProductsByName(String name, Pageable pageable);
    Optional<Product> findById(Long ID);
    Page<Product> search(String contents, Pageable pageable);
    Page<Product> findByBrand(String brand, Pageable pageable);
//    Optional<Product> DetailProduct(Long productid);
    Page<Product> findByCategory(String category, Pageable pageable);
    Optional<Product> update(Product product);
    void deleteById(Long productId);
    Page<Product> brandList(Pageable pageable);
    Product increaseStock(Long productId, Long stock);
    Product selledCancle(Long productId, Long stock);

    Product decreaseStock(Long productId, Long stock);

    Product selledProduct(Long productId, Long stock);

}
