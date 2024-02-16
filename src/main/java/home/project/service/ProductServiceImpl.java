package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;
import home.project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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
    public void validateDuplicateProduct (Product product) {
        productRepository.findById(product.getProduct_id()).ifPresent(m -> { throw new IllegalStateException("이미 존재하는 상품(상품ID 중복)입니다."); });

    }

    public void emptyProduct (Product product) {
        productRepository.findById(product.getProduct_id()).orElseThrow(() -> { throw new IllegalStateException("상품ID를 입력해주세요."); });
        productRepository.findByBrand(Optional.ofNullable(product.getBrand())).orElseThrow(() -> { throw new IllegalStateException("상품의 브랜드를 입력해주세요."); });
        productRepository.findByName(Optional.ofNullable(product.getName())).orElseThrow(() -> { throw new IllegalStateException("상품의 이름을 입력해주세요."); });
        productRepository.findByImage(Optional.ofNullable(product.getImage())).orElseThrow(() -> { throw new IllegalStateException("이미지를 입력해주세요."); });

    }

    public void productConfirm (Product product){
        productRepository.save(product);
    }
}
