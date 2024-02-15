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
    public ProductServiceImpl(ProductRepository productRepository){        this.productRepository=productRepository;    }

    public List<Product> findProductRecom(Product product){
        return productRepository.findTop5BySelledcountOrderBySelledcountDesc(product);
    }
    public void join (Product product){
        validateDuplicateProduct(product);
        productConfirm(product);
    }
    public void validateDuplicateProduct (Product product) {
        productRepository.findById(product.getProduct_id()).ifPresent(m -> { throw new IllegalStateException("이미 존재하는 회원입니다."); });
        productRepository.findByName(Optional.of(product.getName())).ifPresent(m -> { throw new IllegalStateException("이미 존재하는 번호입니다."); });
    }
    public void productConfirm (Product product){
        productRepository.save(product);
    }
}
