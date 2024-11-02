package home.project.service;

import home.project.domain.Product;
import home.project.domain.ProductOrder;
import home.project.domain.elasticsearch.ProductDocument;
import home.project.dto.requestDTO.CreateProductRequestDTO;
import home.project.dto.requestDTO.UpdateProductRequestDTO;
import home.project.dto.responseDTO.*;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.CategoryRepository;
import home.project.repository.ProductOrderRepository;
import home.project.repositoryForElasticsearch.ProductElasticsearchRepository;
import home.project.repository.ProductRepository;
import home.project.util.IndexToElasticsearch;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static home.project.util.CategoryMapper.getCode;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductElasticsearchRepository productElasticsearchRepository;
    private final Converter converter;
    private final KafkaEventProducerService kafkaEventProducerService;
    private final IndexToElasticsearch indexToElasticsearch;
    private final ElasticsearchOperations elasticsearchOperations;



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

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String timeStamp = now.format(formatter);

        Product product = new Product();
        product.setName(createProductRequestDTO.getName());
        product.setBrand(createProductRequestDTO.getBrand());
        product.setCategory(categoryRepository.findByCode(createProductRequestDTO.getCategory())
                .orElseThrow(() -> new IdNotFoundException(createProductRequestDTO.getCategory() + " 카테고리가 없습니다.")));
        product.setStock(createProductRequestDTO.getStock());
        product.setProductNum(timeStamp + createProductRequestDTO.getBrand().charAt(0) + createProductRequestDTO.getName().charAt(0) + createProductRequestDTO.getCategory().toString());
        product.setSoldQuantity(createProductRequestDTO.getSoldQuantity());
        product.setPrice(createProductRequestDTO.getPrice());
        product.setDiscountRate(createProductRequestDTO.getDiscountRate());
        product.setDefectiveStock(createProductRequestDTO.getDefectiveStock());
        product.setDescription(createProductRequestDTO.getDescription());
        product.setCreateAt(LocalDateTime.now());
        product.setImageUrl(createProductRequestDTO.getImageUrl());
        product.setSizes(createProductRequestDTO.getSizes());
        product.setColors(createProductRequestDTO.getColors());

        boolean productNumExists = productRepository.existsByProductNum(product.getProductNum());
        if (productNumExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 품번입니다.");
        }

        productRepository.save(product);

        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);


        try {
            indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public ProductResponse findByIdReturnProductResponse(Long productId) {
        Product product = findById(productId);
        kafkaEventProducerService.sendProductViewLog(productId);
        return converter.convertFromProductToProductResponse(product);
    }

    @Override
    public ProductResponseForManager findByIdReturnProductResponseForManager(Long productId) {
        Product product = findById(productId);
        return converter.convertFromProductToProductResponseForManaging(product);
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
    public Product findByProductNum(String productNum){
        return productRepository.findByProductNum(productNum);
    }

    @Override
    public Page<ProductResponse> findAll(Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findAll(pageable);
        return converter.convertFromPagedProductToPagedProductResponse(pagedProduct);
    }

    @Override
    public Page<ProductResponseForManager> findAllForManaging(Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findAll(pageable);
        return converter.convertFromPagedProductToPagedProductResponseForManaging(pagedProduct);
    }

    @Override
    public Page<ProductResponse> findNewProduct(Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findTop20LatestProducts(pageable);
        return converter.convertFromPagedProductToPagedProductResponse(pagedProduct);
    }

    @Override
    public Page<ProductResponse> findProducts(String brand, String category, String productName, String content,List<String> colors, List<String> sizes,  Pageable pageable) {
        String categoryCode = null;

        if (category != null && !category.isEmpty()) {//?
            categoryCode = getCode(category);
        }
        if (content != null && !content.isEmpty()) {//?
            categoryCode = getCode(content);
        }

        Page<Product> pagedProduct = productRepository.findProducts(brand, categoryCode, productName, content,colors, sizes, pageable);

        return converter.convertFromPagedProductToPagedProductResponse(pagedProduct);
    }

    @Override
    public Page<ProductResponse> findProductsOnElastic(String brand, String category, String productName, String content, Pageable pageable) {
        String categoryCode = null;

        if (category != null && !category.isEmpty()) {//?
            categoryCode = getCode(category);
        }
        if (content != null && !content.isEmpty()) {//?
            categoryCode = getCode(content);
        }

        Page<ProductDocument> pagedDocuments = productElasticsearchRepository.findProducts(brand, categoryCode, productName, content, pageable);

        Page<Product> pagedProduct = pagedDocuments.map(doc -> findById(doc.getId()));

        return converter.convertFromPagedProductToPagedProductResponse(pagedProduct);
    }

    @Override
    public Page<ProductResponseForManager> findProductsForManaging(String brand, String category, String productName, String content,List<String> colors, List<String> sizes,  Pageable pageable) {
        String categoryCode = null;

        if (category != null && !category.isEmpty()) {
            categoryCode = getCode(category);
        }
        if (content != null && !content.isEmpty()) {
            categoryCode = getCode(content);
        }

        Page<Product> pagedProduct = productRepository.findProducts(brand, categoryCode, productName, content,colors, sizes, pageable);

        return converter.convertFromPagedProductToPagedProductResponseForManaging(pagedProduct);
    }

    @Override
    public Page<ProductResponseForManager> findProductsOnElasticForManaging(String brand, String category, String productName, String content, Pageable pageable) {
        String categoryCode = null;

        if (category != null && !category.isEmpty()) {//?
            categoryCode = getCode(category);
        }
        if (content != null && !content.isEmpty()) {//?
            categoryCode = getCode(content);
        }

        Page<ProductDocument> pagedDocuments = productElasticsearchRepository.findProducts(brand, categoryCode, productName, content, pageable);

        Page<Product> pagedProduct = pagedDocuments.map(productDocument -> findById(productDocument.getId()));

        return converter.convertFromPagedProductToPagedProductResponseForManaging(pagedProduct);
    }

    @Override
    public Page<ProductResponseForManager> findProductsForManaging(String brand, String category, String productName, String content, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProductResponseForManager> findSoldProducts(String brand, String category, String productName, String content, List<String> colors, List<String> sizes, Pageable pageable) {
        String categoryCode = null;

        if (category != null && !category.isEmpty()) {
            categoryCode = getCode(category);
        }
        if (content != null && !content.isEmpty()) {
            categoryCode = getCode(content);
        }

        Page<Product> pagedProduct = productRepository.findProducts(brand, categoryCode, productName, content, colors, sizes, pageable);

        return converter.convertFromPagedProductToPagedProductResponseForManaging(pagedProduct);
    }

    @Override
    public Page<ProductResponse> findAllBySoldQuantity(Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findAllBySoldQuantity(pageable);
        return converter.convertFromPagedProductToPagedProductResponse(pagedProduct);
    }

    @Override
    public Page<ProductResponse> brandList(Pageable pageable) {
        Page<Product> pagedProduct = productRepository.findAllByOrderByBrandAsc(pageable);
        return converter.convertFromPagedProductToPagedProductResponse(pagedProduct);
    }

    @Override
    @Transactional
    public ProductResponse update(UpdateProductRequestDTO updateProductRequestDTO) {

        boolean isCategoryModified = false;
        boolean isNameModified = false;
        boolean isBrandModified = false;

        Product existingProduct = findById(updateProductRequestDTO.getId());

        String newProductNum = reCreateProductNum(existingProduct.getProductNum(), updateProductRequestDTO);

        Product beforeUpdate = new Product();
        BeanUtils.copyProperties(existingProduct, beforeUpdate);

        if (updateProductRequestDTO.getStock() != null) {
            if (updateProductRequestDTO.getStock() < 0) {
                throw new IllegalStateException("재고가 음수일 수 없습니다.");
            }
            existingProduct.setStock(updateProductRequestDTO.getStock());
        }

        if (updateProductRequestDTO.getSoldQuantity() != null) {
            if (updateProductRequestDTO.getSoldQuantity() < 0) {
                throw new IllegalStateException("판매량이 음수일 수 없습니다.");
            }
            existingProduct.setSoldQuantity(updateProductRequestDTO.getSoldQuantity());
        }

        if (updateProductRequestDTO.getName() != null) {
            existingProduct.setName(updateProductRequestDTO.getName());
            isNameModified = true;
        }

        if (updateProductRequestDTO.getBrand() != null) {
            existingProduct.setBrand(updateProductRequestDTO.getBrand());
            isBrandModified = true;
        }

        if (updateProductRequestDTO.getPrice() != null) {
            existingProduct.setPrice(updateProductRequestDTO.getPrice());
        }

        if (updateProductRequestDTO.getDiscountRate() != null) {
            existingProduct.setDiscountRate(updateProductRequestDTO.getDiscountRate());
        }

        if (updateProductRequestDTO.getDefectiveStock() != null) {
            existingProduct.setDefectiveStock(updateProductRequestDTO.getDefectiveStock());
        }

        if (updateProductRequestDTO.getDescription() != null) {
            existingProduct.setDescription(updateProductRequestDTO.getDescription());
        }

        if (updateProductRequestDTO.getImageUrl() != null) {
            existingProduct.setImageUrl(updateProductRequestDTO.getImageUrl());
        }

        if (updateProductRequestDTO.getCategory() != null) {
            existingProduct.setCategory(categoryRepository.findByCode(updateProductRequestDTO.getCategory()).orElseThrow(() -> new IdNotFoundException(updateProductRequestDTO.getCategory() + "(으)로 등록된 카테고리가 없습니다.")));
            isCategoryModified = true;
        }

        if (updateProductRequestDTO.getSizes() != null && !updateProductRequestDTO.getSizes().isEmpty()) {
            existingProduct.setSizes(updateProductRequestDTO.getSizes());
        }

        if (updateProductRequestDTO.getColors() != null && !updateProductRequestDTO.getColors().isEmpty()) {
            existingProduct.setColors(updateProductRequestDTO.getColors());
        }

        if (existingProduct.equals(beforeUpdate)) {
            throw new NoChangeException("변경된 상품 정보가 없습니다.");
        }

        if(isBrandModified || isNameModified || isCategoryModified){
            if (!newProductNum.equals(existingProduct.getProductNum())) {
                if (productRepository.existsByProductNum(newProductNum)) {
                    throw new DataIntegrityViolationException("이미 사용 중인 품번입니다.");
                }
                existingProduct.setProductNum(newProductNum);
            }
        }

        Product product = productRepository.save(existingProduct);

        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
        indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);

        return converter.convertFromProductToProductResponse(product);
    }

    @Override
    @Transactional
    public String deleteById(Long productId) {
        Product product =findById(productId);
        String name = product.getName();
        productRepository.deleteById(productId);
        elasticsearchOperations.delete(String.valueOf(productId), ProductDocument.class);
        return name;
    }

    @Override
    @Transactional
    public ProductResponseForManager increaseStock(Long productId, Long stock) {
        if (stock < 0) {
            throw new IllegalStateException("재고가 음수일 수 없습니다.");
        }
        Product product = findById(productId);
        Long currentStock = product.getStock();
        Long newStock = currentStock + stock;
        product.setStock(newStock);
        productRepository.save(product);
        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
        indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
        return converter.convertFromProductToProductResponseForManaging(product);
    }

    @Override
    @Transactional
    public ProductResponseForManager decreaseStock(Long productId, Long stock) {
        Product product = findById(productId);
        Long currentStock = product.getStock();
        Long newStock = Math.max(currentStock - stock, 0);
        if (currentStock <= 0 || stock > currentStock) {
            throw new DataIntegrityViolationException("재고가 부족합니다.");
        }
        product.setStock(newStock);
        productRepository.save(product);
        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
        indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
        return converter.convertFromProductToProductResponseForManaging(product);
    }

    @Override
    @Transactional
    public ProductResponseForManager increaseSoldQuantity(Long productId, Long quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("증가시킬 판매 수량은 음수일 수 없습니다.");
        }
        Product product = findById(productId);
        Long currentSoldQuantity = product.getSoldQuantity();
        Long newSoldQuantity = currentSoldQuantity + quantity;
        product.setSoldQuantity(newSoldQuantity);
        productRepository.save(product);
        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
        indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
        return converter.convertFromProductToProductResponseForManaging(product);
    }

    @Override
    @Transactional
    public ProductResponseForManager decreaseSoldQuantity(Long productId, Long quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("감소시킬 판매 수량은 음수일 수 없습니다.");
        }
        Product product = findById(productId);
        Long currentSoldQuantity = product.getSoldQuantity();
        if (currentSoldQuantity < quantity) {
            throw new DataIntegrityViolationException("감소시킬 판매 수량이 현재 판매 수량보다 많습니다.");
        }
        Long newSoldQuantity = currentSoldQuantity - quantity;
        product.setSoldQuantity(newSoldQuantity);
        productRepository.save(product);
        ProductDocument productDocument = converter.convertFromProductToProductDocument(product);
        indexToElasticsearch.indexDocumentToElasticsearch(productDocument, ProductDocument.class);
        return converter.convertFromProductToProductResponseForManaging(product);
    }


    private String reCreateProductNum(String oldProductNum, UpdateProductRequestDTO updateProductRequestDTO){
        String frontOfOldProductNum = oldProductNum.substring(0,12);
        String middleOfNewProductNum = "" + updateProductRequestDTO.getBrand().charAt(0) + updateProductRequestDTO.getName().charAt(0);
        String newCategory = updateProductRequestDTO.getCategory();
        return frontOfOldProductNum+middleOfNewProductNum+newCategory;
    }

    @Override
    public Product findByProductOrderNum(Long productOrderId) {
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new IdNotFoundException(productOrderId + "(으)로 등록된 주문서가 없습니다."));
        return productOrder.getProduct();
    }

}