package home.project.repositoryForElasticsearch;

import home.project.domain.elasticsearch.MemberDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MemberElasticsearchRepository extends ElasticsearchRepository<MemberDocument, Long>, MemberElasticsearchRepositoryCustom {

}
