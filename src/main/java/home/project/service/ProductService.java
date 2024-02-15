package home.project.service;

import home.project.domain.Product;

import java.util.List;

public interface ProductService {
    List<Product> findProductRecom(Product product);
}
