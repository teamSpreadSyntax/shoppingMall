package home.project.repositoryForElasticsearch;

import home.project.domain.elasticsearch.MemberDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberElasticsearchRepositoryCustom {
    Page<MemberDocument> findMembers(String name, String email, String phone, String role, String content, Pageable pageable);

}
