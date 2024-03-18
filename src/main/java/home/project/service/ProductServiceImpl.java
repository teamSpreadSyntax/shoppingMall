package home.project.service;

import home.project.domain.Product;
import home.project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository){
        this.productRepository=productRepository;
    }

    public void join (Product product){
        productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findByname(String name) {
        Product product = productRepository.findByname(name).orElseThrow(() -> { throw new IllegalStateException(name+"으로 가입된 회원이 없습니다."); });
        return Optional.ofNullable(product);
    }
    public Optional<List<Product>> findByBrand(String brand){
        List<Product> product = productRepository.findByBrand(brand).orElseThrow(() -> { throw new IllegalStateException(brand+"브랜드가 없습니다."); });
        return Optional.ofNullable(product);
    }

    public Optional<List<Product>> findByCategory(String category) {
        List<Product> product = productRepository.findByCategory(category).orElseThrow(() -> { throw new IllegalStateException(category+"카테고리에 상품이 없습니다."); });
        return Optional.ofNullable(product);
    }

    public Optional<Product> DetailProduct (Long productid){
        Product existProduct = productRepository.findById(productid).orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));
        return Optional.ofNullable(existProduct);
    }

    public void update (Product product){
        Product exsitsProduct = productRepository.findById(product.getId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));
        exsitsProduct.setBrand(product.getBrand());
        exsitsProduct.setName(product.getName());
        exsitsProduct.setSelledcount(product.getSelledcount());
        exsitsProduct.setImage(product.getImage());
        exsitsProduct.setStock(product.getStock());
        exsitsProduct.setCategory(product.getCategory());
        productRepository.save(exsitsProduct);
    }

    public void deleteById (Long productid){
        productRepository.findById(productid).orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));
        productRepository.deleteById(productid);
        System.out.println("삭제가 완료되었습니다");
    }

    public List<String> brandList(){
        return productRepository.findAllByOrderByBrandAsc();
    }

    public Product increaseStock(Long productId , Long stock) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        Long currentStock = product.getStock();
        Long newStock = currentStock + stock;
        product.setStock(newStock);
        return productRepository.save(product);

    }

    public Product selledCancle(Long productId , Long stock) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        Long currentStock = product.getStock();
        Long newStock = Math.max(currentStock + stock, 0);
        product.setStock(newStock);
        Long allCount = product.getSelledcount();
        product.setSelledcount(allCount + (currentStock - newStock));
        return productRepository.save(product);
    }

    public Product decreaseStock(Long productId , Long stock) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        Long currentStock = product.getStock();
        Long newStock = Math.max(currentStock - stock, 0);
        product.setStock(newStock);
        Long allCount = product.getSelledcount();
        product.setSelledcount(allCount + (currentStock - newStock));
        return productRepository.save(product);
    }
}
