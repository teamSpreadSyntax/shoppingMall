package home.project.repositoryForElasticsearch;

import home.project.domain.elasticsearch.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductElasticsearchRepositoryCustom {
    Page<ProductDocument> findProducts(String brand, String category, String productName, String content, Pageable pageable);

}
