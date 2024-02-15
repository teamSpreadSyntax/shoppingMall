package home.project.service;

import home.project.domain.Product;
import home.project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository){        this.productRepository=productRepository;    }

    public List<Product> findProductRecom(Product product){
        return productRepository.findTop5BySelledcountOrderBySelledcountDesc(product);
    }
}
