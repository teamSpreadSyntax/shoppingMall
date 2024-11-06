package home.project.service;

import home.project.domain.Product;
import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.ProductResponseForManager;
import home.project.dto.responseDTO.ProductWithQnAAndReviewResponse;
import home.project.dto.responseDTO.ProductWithQnAAndReviewResponseForManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ProductService {

    void join(CreateProductRequestDTO createProductRequestDTO);

    Product findById(Long id);

    ProductWithQnAAndReviewResponse findByIdReturnProductResponse(Long productId);

    ProductWithQnAAndReviewResponseForManager findByIdReturnProductResponseForManager(Long productId);

    Page<ProductResponseForManager> findAllByIdReturnProductResponseForManager(Pageable pageable);

    Product findByProductNum(String productNum);

    Page<ProductResponse> findAll(Pageable pageable);


    Page<ProductResponseForManager> findAllForManaging(Pageable pageable);

    Page<ProductResponse> adminFindNewProduct(Pageable pageable);

    Page<ProductResponse> findNewProduct(Pageable pageable);

    Page<ProductResponse> findProducts(String brand, String category, String productName, String content, String color, String size, Pageable pageable);

    Page<ProductResponse> findProductsOnElastic(String brand, String category, String productName, String content, Pageable pageable);

    Page<ProductResponseForManager> findProductsForManaging(String brand, String category, String productName, String content, String color, String size, Pageable pageable);
    Page<ProductResponseForManager> findProductsOnElasticForManaging(String brand, String category, String productName, String content, Pageable pageable);

    Page<ProductResponseForManager> findProductsForManaging(String brand, String category, String productName, String content, Pageable pageable);

    Page<ProductResponseForManager> findSoldProducts(String brand, String category, String productName, String content,String color, String size,  Pageable pageable);

    Page<ProductResponse> findAllBySoldQuantity(Pageable pageable);

    Page<String> brandList(Pageable pageable);

    ProductResponse update(UpdateProductRequestDTO updateProductRequestDTO);

    String deleteById(Long productId);

    ProductResponseForManager increaseStock(Long productId, Long stock);

    ProductResponseForManager decreaseStock(Long productId, Long stock);

    ProductResponseForManager increaseSoldQuantity(Long productId, Long stock);

    ProductResponseForManager decreaseSoldQuantity(Long productId, Long stock);

    Product findByProductOrderNum(Long productOrderId);

    ProductResponse updateMyProduct(UpdateProductRequestDTO updateProductRequestDTO);

    String deleteByIdForAdmin(Long productId);

    ProductResponseForManager increaseStockForAdmin(Long productId, Long stock);

    ProductResponseForManager decreaseStockForAdmin(Long productId, Long stock);

    ProductResponseForManager increaseSoldQuantityForAdmin(Long productId, Long quantity);

    ProductResponseForManager decreaseSoldQuantityForAdmin(Long productId, Long quantity);

    Page<ProductResponseForManager> findProductsOnElasticForAdmin(String brand, String category, String productName, String content, Pageable pageable);

    Product findByProductOrderNumForAdmin(Long productOrderId);
}
