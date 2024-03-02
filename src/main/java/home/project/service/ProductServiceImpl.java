package home.project.service;

import home.project.domain.Member;
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

    public List<String> findProductRecom(){
        return productRepository.findTop5ByOrderBySelledcountDesc();
    }

    public List<String> brandList(){
        return productRepository.findAllByOrderByBrandAsc();
    }

    public void join (Product product){
        validateDuplicateProduct(product);
        productConfirm(product);
    }

    public void update (Product product){
        Product exsitsProduct = productRepository.findById(product.getProduct_id()).orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));
        exsitsProduct.setBrand(product.getBrand());
        exsitsProduct.setName(product.getName());
        exsitsProduct.setSelledcount(product.getSelledcount());
        exsitsProduct.setImage(product.getImage());
        productRepository.save(exsitsProduct);
    }
    public void delete (Product product){
        if(product.getProduct_id() == null) {
//
        }

        productRepository.deleteById(product.getProduct_id());
    }

    public void validateDuplicateProduct (Product product) {
        productRepository.findById(product.getProduct_id()).ifPresent(m -> { throw new IllegalStateException("이미 존재하는 상품(상품ID 중복)입니다."); });
    }

    public void emptyProduct (Product product) {
        if(product.getProduct_id() == null) {
            throw new IllegalStateException("상품ID를 입력해주세요.");
        }
        if(product.getBrand() == null || product.getBrand().isBlank()) {
            throw new IllegalStateException("상품의 브랜드를 입력해주세요.");
        }
        if(product.getName() == null || product.getName().isBlank()) {
            throw new IllegalStateException("상품의 이름 입력해주세요.");
        }
        if(product.getImage() == null || product.getImage().isBlank()) {
            throw new IllegalStateException("상품의 이미지를 입력해주세요.");
        }

    }

    public void productConfirm (Product product){
        productRepository.save(product);
    }
}
