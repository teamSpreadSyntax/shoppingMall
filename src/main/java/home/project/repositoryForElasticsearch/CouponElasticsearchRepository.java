package home.project.repositoryForElasticsearch;

import home.project.domain.elasticsearch.CouponDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponElasticsearchRepository extends ElasticsearchRepository<CouponDocument, Long>,
        CouponElasticsearchRepositoryCustom {
}