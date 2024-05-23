package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;
import home.project.domain.ProductDTOWithBrandId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface ProductService {
    void join(Product product);
    Page<Product> findAll(Pageable pageable);
    Optional<Product> findByName(String name);
    Optional<Product> findById(Long ID);
    Optional<Page<Product>> search(String contents, Pageable pageable);
    Optional<Page<Product>> findByBrand(String brand, Pageable pageable);
//    Optional<Product> DetailProduct(Long productid);
    Optional<Page<Product>> findByCategory(String category, Pageable pageable);
    Optional<Product> update(Product product);
    void deleteByName(String productName);
    List<ProductDTOWithBrandId> brandList();
    Product increaseStock(Long productId, Long stock);

    Product selledCancle(Long productId, Long stock);

    Product decreaseStock(Long productId, Long stock);

    Product selledProduct(Long productId, Long stock);

}
