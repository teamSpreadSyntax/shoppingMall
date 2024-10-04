package home.project.service;

import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import home.project.dto.responseDTO.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface ProductService {

    void join(CreateProductRequestDTO createProductRequestDTO);

    ProductResponse findByIdReturnProductResponse(Long productId);

    Page<ProductResponse> findAll(Pageable pageable);

    Page<ProductResponse> findProducts(String brand, String category, String productName, String content, Pageable pageable);

    Page<ProductResponse> brandList(Pageable pageable);

    ProductResponse update(UpdateProductRequestDTO updateProductRequestDTO);

    void deleteById(Long productId);

    ProductResponse increaseStock(Long productId, Long stock);

    ProductResponse decreaseStock(Long productId, Long stock);

}
