package home.project.repositoryForElasticsearch;

import home.project.domain.elasticsearch.MemberDocument;
import home.project.domain.elasticsearch.OrdersDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface OrdersElasticsearchRepository extends ElasticsearchRepository<OrdersDocument, Long>, OrdersElasticsearchRepositoryCustom {

}
