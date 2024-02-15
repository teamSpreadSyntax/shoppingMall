package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;

import java.util.List;

public interface ProductService {
    List<Product> findProductRecom();

    void join(Product product);
    void validateDuplicateProduct(Product product);
    void productConfirm(Product product);
}
