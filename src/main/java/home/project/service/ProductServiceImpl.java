package home.project.service;

import home.project.domain.Product;
import home.project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository){
        this.productRepository=productRepository;
    }

    public void join (Product product){ productRepository.save(product); }

    public Optional<Product> findById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> { throw new IllegalArgumentException(productId+"로 등록된 상품이 없습니다."); });
        return Optional.ofNullable(product);
    }

    public Page<Product> findAll(Pageable pageable) { return productRepository.findAll(pageable); }

    public Page<Product> findProducts(String brand, String category, String productName, String content, Pageable pageable) {
        Page<Product> productPage = productRepository.findProducts(brand ,category, productName, content, pageable);
        if (productPage.getSize()==0||productPage.getTotalElements()==0) { throw new IllegalArgumentException("해당하는 상품이 없습니다."); }
        return productRepository.findProducts(brand, category, productName, content, pageable);
    }

    public Page<Product> brandList(Pageable pageable){
        Page<Product> brandList = productRepository.findAllByOrderByBrandAsc(pageable);
        return brandList;
    }

    public Optional<Product> update (Product product){
        Product exsitsProduct = productRepository.findById(product.getId()).orElseThrow(() -> new IllegalArgumentException(product.getId()+"로 등록된 상품이 없습니다."));
        exsitsProduct.setBrand(product.getBrand());
        exsitsProduct.setName(product.getName());
        exsitsProduct.setSelledcount(product.getSelledcount());
        exsitsProduct.setImage(product.getImage());
        exsitsProduct.setStock(product.getStock());
        exsitsProduct.setCategory(product.getCategory());
        Long currentStock = product.getStock();
        if (currentStock < 0 || exsitsProduct.getStock() > currentStock){throw new DataIntegrityViolationException("재고가 음수 일 수 없습니다.");}
        productRepository.save(exsitsProduct);
        Optional<Product> newProduct = productRepository.findById(exsitsProduct.getId());
        return newProduct;
    }

    public void deleteById (Long productId){
        productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException(productId+"로 등록된 상품이 없습니다."));
        productRepository.deleteById(productId);
    }

    public Product increaseStock(Long productId , Long stock) {
        if (stock < 0) { throw new DataIntegrityViolationException("재고가 음수일 수 없습니다."); }
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException(productId+"로 등록된 상품이 없습니다."));
        Long currentStock = product.getStock();
        Long newStock = currentStock + stock;
        product.setStock(newStock);
        return productRepository.save(product);
    }

    public Product decreaseStock(Long productId , Long stock) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException(productId+"로 등록된 상품이 없습니다."));
        Long currentStock = product.getStock();
        Long newStock = Math.max(currentStock - stock, 0);
        if (currentStock <= 0 || stock > currentStock) {throw new DataIntegrityViolationException("재고가 부족합니다.");}
        product.setStock(newStock);
        return productRepository.save(product);
    }


    }
