package home.project.repositoryForElasticsearch;

import home.project.domain.elasticsearch.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
public interface ProductElasticsearchRepository extends ElasticsearchRepository<ProductDocument, Long>, ProductElasticsearchRepositoryCustom {
    List<ProductDocument> findByNameContaining(String name);
    List<ProductDocument> findByBrand(String brand);
}
