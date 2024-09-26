package home.project.service;

import home.project.domain.Product;
import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface ProductService {

    void join(CreateProductRequestDTO createProductRequestDTO);

    Optional<Product> findById(Long productId);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findProducts(String brand, String category, String productName, String content, Pageable pageable);

    String stringBuilder(String brand, String category, String productName, String content, Page<Product> productPage);

Page<Product> brandList(Pageable pageable);

    Optional<Product> update(@Valid UpdateProductRequestDTO updateProductRequestDTO);

    void deleteById(Long productId);

    Product increaseStock(Long productId, Long stock);

    Product decreaseStock(Long productId, Long stock);

}
