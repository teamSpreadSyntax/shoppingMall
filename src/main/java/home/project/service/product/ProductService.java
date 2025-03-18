package home.project.service.product;

import home.project.domain.product.Product;
import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import home.project.dto.responseDTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ProductService {

    void join(CreateProductRequestDTO createProductRequestDTO,MultipartFile mainImageFile, List<MultipartFile> descriptionImages);

    Product findById(Long id);

    ProductWithQnAAndReviewResponse findByIdReturnProductResponse(Long productId);

    ProductWithQnAAndReviewResponseForManager findByIdReturnProductResponseForManager(Long productId);

    Page<ProductSimpleResponseForManager> findAllByIdReturnProductResponseForManager(Pageable pageable);

    Product findByProductNum(String productNum);

    Page<ProductSimpleResponse> findAll(Pageable pageable);


    Page<ProductResponseForManager> findAllForManaging(Pageable pageable);

    Page<ProductSimpleResponseForManager> adminFindNewProduct(Pageable pageable);

    Page<ProductSimpleResponse> findNewProduct(Pageable pageable);

    Page<ProductResponse> findProducts(String brand, String category, String productName, String content, String color, String size, Pageable pageable);

    Page<ProductSimpleResponse> findProductsOnElastic(String brand, String category, String productName, String content, Pageable pageable);
    Page<ProductSimpleResponse> findProducts(String brand, String category, String productName, String content, Pageable pageable);

    Page<ProductResponseForManager> findProductsForManaging(String brand, String category, String productName, String content, String color, String size, Pageable pageable);
    Page<ProductResponseForManager> findProductsOnElasticForManaging(String brand, String category, String productName, String content, Pageable pageable);

    Page<ProductResponseForManager> findProductsForManaging(String brand, String category, String productName, String content, Pageable pageable);

    Page<ProductResponseForManager> findSoldProducts(String brand, String category, String productName, String content,String color, String size,  Pageable pageable);

    Page<ProductResponse> findAllBySoldQuantity(Pageable pageable);

    Page<String> brandList(Pageable pageable);

    ProductResponse update(UpdateProductRequestDTO updateProductRequestDTO,MultipartFile mainImageFile, List<MultipartFile> descriptionImages);

    String deleteById(Long productId);

    ProductResponseForManager increaseStock(Long productId, Long stock);

    ProductResponseForManager decreaseStock(Long productId, Long stock);

    ProductResponseForManager increaseSoldQuantity(Long productId, Long stock);

    ProductResponseForManager decreaseSoldQuantity(Long productId, Long stock);

    Product findByProductIdAndConfirmHasPurchase(Long productOrderId);

    ProductResponse updateMyProduct(UpdateProductRequestDTO updateProductRequestDTO , MultipartFile mainImageFile, List<MultipartFile> descriptionImages);

    String deleteByIdForAdmin(Long productId);

    ProductResponseForManager increaseStockForAdmin(Long productId, Long stock);

    ProductResponseForManager decreaseStockForAdmin(Long productId, Long stock);

    ProductResponseForManager increaseSoldQuantityForAdmin(Long productId, Long quantity);

    ProductResponseForManager decreaseSoldQuantityForAdmin(Long productId, Long quantity);

    Page<ProductResponseForManager> findProductsOnElasticForAdmin(String brand, String category, String productName, String content, Pageable pageable);

    Product findByProductOrderNumForAdmin(Long productOrderId);
}
