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

    public List<String> brandList(){
        return productRepository.findAllByOrderByBrandAsc();
    }
    public void join (Product product){
        productRepository.save(product);
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



}
