package home.project.repositoryForElasticsearch;

import home.project.domain.elasticsearch.OrdersDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface OrdersElasticsearchRepository extends ElasticsearchRepository<OrdersDocument, Long>, OrdersElasticsearchRepositoryCustom {

}
