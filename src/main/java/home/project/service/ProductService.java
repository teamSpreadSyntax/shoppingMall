package home.project.service;

import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import home.project.dto.responseDTO.ProductResponse;
import home.project.dto.responseDTO.ProductResponseForManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface ProductService {

    void join(CreateProductRequestDTO createProductRequestDTO);

    ProductResponse findByIdReturnProductResponse(Long productId);

    ProductResponseForManager findByIdReturnProductResponseForManager(Long productId);

    Page<ProductResponse> findAll(Pageable pageable);

    Page<ProductResponseForManager> findAllForManaging(Pageable pageable);

    Page<ProductResponse> findProducts(String brand, String category, String productName, String content, Pageable pageable);

    Page<ProductResponseForManager> findProductsForManaging(String brand, String category, String productName, String content, Pageable pageable);


    Page<ProductResponse> brandList(Pageable pageable);

    ProductResponse update(UpdateProductRequestDTO updateProductRequestDTO);

    void deleteById(Long productId);

    ProductResponseForManager increaseStock(Long productId, Long stock);

    ProductResponseForManager decreaseStock(Long productId, Long stock);

}
