package home.project.service;

import home.project.domain.Product;
import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void join(CreateProductRequestDTO createProductRequestDTO) {

        Long currentStock = createProductRequestDTO.getStock();
        Long currentSoldQuantity = createProductRequestDTO.getSoldQuantity();
        if (currentStock < 0) {
            throw new IllegalStateException("재고가 음수일 수 없습니다.");
        } else if (currentSoldQuantity < 0) {
            throw new IllegalStateException("판매량이 음수일 수 없습니다.");
        }

        Product product = new Product();
        product.setBrand(createProductRequestDTO.getBrand());
        product.setCategory(createProductRequestDTO.getCategory());
        product.setProductNum(createProductRequestDTO.getBrand().charAt(0) + createProductRequestDTO.getName().charAt(0) + createProductRequestDTO.getCategory());
        product.setSoldQuantity(createProductRequestDTO.getSoldQuantity());
        product.setName(createProductRequestDTO.getName());
        product.setStock(createProductRequestDTO.getStock());

        boolean productNumExists = productRepository.existsByProductNum(product.getProductNum());
        if (productNumExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 품번입니다.");
        }

        productRepository.save(product);
    }

    @Override
    public Product findById(Long productId) {
        if (productId == null) {
            throw new IllegalStateException("id가 입력되지 않았습니다.");
        }

        return productRepository.findById(productId)
                .orElseThrow(() -> new IdNotFoundException(productId + "(으)로 등록된 상품이 없습니다."));
    }


    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findProducts(String brand, String category, String productName, String content, Pageable pageable) {
        return productRepository.findProducts(brand, category, productName, content, pageable);
    }

    @Override
    public String stringBuilder(String brand, String category, String productName, String content, Page<Product> productPage) {
        StringBuilder searchCriteria = new StringBuilder();
        if (brand != null) searchCriteria.append(brand).append(", ");
        if (category != null) searchCriteria.append(category).append(", ");
        if (productName != null) searchCriteria.append(productName).append(", ");
        if (content != null) searchCriteria.append(content).append(", ");

        String successMessage;
        if (!searchCriteria.isEmpty()) {
            searchCriteria.setLength(searchCriteria.length() - 2);
            successMessage = "검색 키워드 : " + searchCriteria;
        } else {
            successMessage = "전체 상품입니다.";
        }
        long totalCount = productPage.getTotalElements();

        if (totalCount == 0) {
            successMessage = "검색 결과가 없습니다. 검색 키워드 : " + searchCriteria;
        }
        return successMessage;
    }

    @Override
    public Page<Product> brandList(Pageable pageable) {

        return productRepository.findAllByOrderByBrandAsc(pageable);
    }

    @Override
    @Transactional
    public Product update(@Valid UpdateProductRequestDTO updateProductRequestDTO) {
        Product existingProduct = findById(updateProductRequestDTO.getId());

        boolean isModified = false;
        boolean isProductNumDuplicate = false;

        if (updateProductRequestDTO.getBrand() != null && !Objects.equals(existingProduct.getBrand(), updateProductRequestDTO.getBrand())) {
            existingProduct.setBrand(updateProductRequestDTO.getBrand());
            isModified = true;
        }

        if (updateProductRequestDTO.getName() != null && !Objects.equals(existingProduct.getName(), updateProductRequestDTO.getName())) {
            existingProduct.setName(updateProductRequestDTO.getName());
            isModified = true;
        }

        if (updateProductRequestDTO.getSoldQuantity() != null && !Objects.equals(existingProduct.getSoldQuantity(), updateProductRequestDTO.getSoldQuantity())) {
            if (updateProductRequestDTO.getSoldQuantity() < 0) {
                throw new IllegalStateException("판매량이 음수일 수 없습니다.");
            }
            existingProduct.setSoldQuantity(updateProductRequestDTO.getSoldQuantity());
            isModified = true;
        }

        if (updateProductRequestDTO.getProductNum() != null && !Objects.equals(existingProduct.getProductNum(), updateProductRequestDTO.getProductNum())) {
            if (productRepository.existsByProductNum(updateProductRequestDTO.getProductNum())) {
                isProductNumDuplicate = true;
            } else {
                existingProduct.setProductNum(updateProductRequestDTO.getProductNum());
                isModified = true;
            }
        }

        if (updateProductRequestDTO.getStock() != null && !Objects.equals(existingProduct.getStock(), updateProductRequestDTO.getStock())) {
            if (updateProductRequestDTO.getStock() < 0) {
                throw new IllegalStateException("재고가 음수일 수 없습니다.");
            }
            existingProduct.setStock(updateProductRequestDTO.getStock());
            isModified = true;
        }

        if (updateProductRequestDTO.getCategory() != null && !Objects.equals(existingProduct.getCategory(), updateProductRequestDTO.getCategory())) {
            existingProduct.setCategory(updateProductRequestDTO.getCategory());
            isModified = true;
        }

        if (isProductNumDuplicate) {
            throw new DataIntegrityViolationException("이미 사용 중인 품번입니다.");
        }

        if (!isModified) {
            throw new NoChangeException("변경된 상품 정보가 없습니다.");
        }

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteById(Long productId) {
        findById(productId);
        productRepository.deleteById(productId);
    }

    @Override
    @Transactional
    public Product increaseStock(Long productId, Long stock) {
        if (stock < 0) {
            throw new IllegalStateException("재고가 음수일 수 없습니다.");
        }
        Product product = findById(productId);
        Long currentStock = product.getStock();
        Long newStock = currentStock + stock;
        product.setStock(newStock);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product decreaseStock(Long productId, Long stock) {
        Product product = findById(productId);
        Long currentStock = product.getStock();
        Long newStock = Math.max(currentStock - stock, 0);
        if (currentStock <= 0 || stock > currentStock) {
            throw new DataIntegrityViolationException("재고가 부족합니다.");
        }
        product.setStock(newStock);
        return productRepository.save(product);
    }


}
