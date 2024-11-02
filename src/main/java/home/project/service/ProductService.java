package home.project.service;

import home.project.domain.Product;
import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.ProductResponseForManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ProductService {

    void join(CreateProductRequestDTO createProductRequestDTO);

    Product findById(Long id);

    ProductResponse findByIdReturnProductResponse(Long productId);

    ProductResponseForManager findByIdReturnProductResponseForManager(Long productId);

    Product findByProductNum(String productNum);

    Page<ProductResponse> findAll(Pageable pageable);

    Page<ProductResponseForManager> findAllForManaging(Pageable pageable);

    Page<ProductResponse> findNewProduct(Pageable pageable);

    Page<ProductResponse> findProducts(String brand, String category, String productName, String content,List<String> colors, List<String> sizes, Pageable pageable);

    Page<ProductResponse> findProductsOnElastic(String brand, String category, String productName, String content, Pageable pageable);

    Page<ProductResponseForManager> findProductsForManaging(String brand, String category, String productName, String content, List<String> colors, List<String> sizes, Pageable pageable);

    Page<ProductResponseForManager> findSoldProducts(String brand, String category, String productName, String content,List<String> colors, List<String> sizes,  Pageable pageable);

    Page<ProductResponse> findAllBySoldQuantity(Pageable pageable);

    Page<ProductResponse> brandList(Pageable pageable);

    ProductResponse update(UpdateProductRequestDTO updateProductRequestDTO);

    String deleteById(Long productId);

    ProductResponseForManager increaseStock(Long productId, Long stock);

    ProductResponseForManager decreaseStock(Long productId, Long stock);

    ProductResponseForManager increaseSoldQuantity(Long productId, Long stock);

    ProductResponseForManager decreaseSoldQuantity(Long productId, Long stock);

    Product findByProductOrderNum(Long productOrderId);
}
