package home.project.service;

import home.project.domain.Product;
import home.project.dto.ProductDTOWithoutId;
import home.project.exceptions.IdNotFoundException;
import home.project.exceptions.NoChangeException;
import home.project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void join(ProductDTOWithoutId productDTOWithoutId) {

        Long currentStock = productDTOWithoutId.getStock();
        Long currentSoldQuantity = productDTOWithoutId.getSoldQuantity();
        if (currentStock < 0) {
            throw new IllegalStateException("재고가 음수일 수 없습니다.");
        } else if (currentSoldQuantity < 0) {
            throw new IllegalStateException("판매량이 음수일 수 없습니다.");
        }

        Product product = new Product();
        product.setBrand(productDTOWithoutId.getBrand());
        product.setCategory(productDTOWithoutId.getCategory());
        product.setProductNum(productDTOWithoutId.getBrand().substring(0, 1) + productDTOWithoutId.getName().substring(0, 1) + productDTOWithoutId.getCategory());
        product.setSoldQuantity(productDTOWithoutId.getSoldQuantity());
        product.setName(productDTOWithoutId.getName());
        product.setStock(productDTOWithoutId.getStock());

        boolean productNumExists = productRepository.existsByProductNum(product.getProductNum());
        if (productNumExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 품번입니다.");
        }

        productRepository.save(product);
    }

    public Optional<Product> findById(Long productId) {
        if (productId == null) {
            throw new IllegalStateException("id가 입력되지 않았습니다.");
        }
        return Optional.ofNullable(productRepository.findById(productId)
                .orElseThrow(() -> new IdNotFoundException(productId + "(으)로 등록된 상품이 없습니다.")));
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> findProducts(String brand, String category, String productName, String content, Pageable pageable) {
        Page<Product> productPage = productRepository.findProducts(brand, category, productName, content, pageable);
        return productPage;
    }

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

    public Page<Product> brandList(Pageable pageable) {
        Page<Product> brandList = productRepository.findAllByOrderByBrandAsc(pageable);
        return brandList;
    }

    public Optional<Product> update(Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IdNotFoundException(product.getId() + "(으)로 등록된 상품이 없습니다."));

        boolean isModified = false;
        boolean isProductNumDuplicate = false;

        if (product.getBrand() != null && !Objects.equals(existingProduct.getBrand(), product.getBrand())) {
            existingProduct.setBrand(product.getBrand());
            isModified = true;
        }

        if (product.getName() != null && !Objects.equals(existingProduct.getName(), product.getName())) {
            existingProduct.setName(product.getName());
            isModified = true;
        }

        if (product.getSoldQuantity() != null && !Objects.equals(existingProduct.getSoldQuantity(), product.getSoldQuantity())) {
            if (product.getSoldQuantity() < 0) {
                throw new IllegalStateException("판매량이 음수일 수 없습니다.");
            }
            existingProduct.setSoldQuantity(product.getSoldQuantity());
            isModified = true;
        }

        if (product.getProductNum() != null && !Objects.equals(existingProduct.getProductNum(), product.getProductNum())) {
            if (productRepository.existsByProductNum(product.getProductNum())) {
                isProductNumDuplicate = true;
            } else {
                existingProduct.setProductNum(product.getProductNum());
                isModified = true;
            }
        }

        if (product.getStock() != null && !Objects.equals(existingProduct.getStock(), product.getStock())) {
            if (product.getStock() < 0) {
                throw new IllegalStateException("재고가 음수일 수 없습니다.");
            }
            existingProduct.setStock(product.getStock());
            isModified = true;
        }

        if (product.getCategory() != null && !Objects.equals(existingProduct.getCategory(), product.getCategory())) {
            existingProduct.setCategory(product.getCategory());
            isModified = true;
        }

        if (isProductNumDuplicate) {
            throw new DataIntegrityViolationException("이미 사용 중인 품번입니다.");
        }

        if (!isModified) {
            throw new NoChangeException("변경된 상품 정보가 없습니다.");
        }

        return Optional.of(productRepository.save(existingProduct));
    }

    public void deleteById(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new IdNotFoundException(productId + "(으)로 등록된 상품이 없습니다."));
        productRepository.deleteById(productId);
    }

    public Product increaseStock(Long productId, Long stock) {
        if (stock < 0) {
            throw new IllegalStateException("재고가 음수일 수 없습니다.");
        }
        Product product = productRepository.findById(productId).orElseThrow(() -> new IdNotFoundException(productId + "(으)로 등록된 상품이 없습니다."));
        Long currentStock = product.getStock();
        Long newStock = currentStock + stock;
        product.setStock(newStock);
        return productRepository.save(product);
    }

    public Product decreaseStock(Long productId, Long stock) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IdNotFoundException(productId + "(으)로 등록된 상품이 없습니다."));
        Long currentStock = product.getStock();
        Long newStock = Math.max(currentStock - stock, 0);
        if (currentStock <= 0 || stock > currentStock) {
            throw new DataIntegrityViolationException("재고가 부족합니다.");
        }
        product.setStock(newStock);
        return productRepository.save(product);
    }


}
