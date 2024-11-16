package home.project.repositoryForElasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import home.project.domain.elasticsearch.MemberDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.util.StringUtils;

import java.util.List;

public class MemberElasticsearchRepositoryCustomImpl implements MemberElasticsearchRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;

    public MemberElasticsearchRepositoryCustomImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public Page<MemberDocument> findMembers(String name, String email, String phone, String role, String content, Pageable pageable) {
        BoolQuery.Builder queryBuilder = new BoolQuery.Builder();

        // 검색 조건이 하나도 없으면 match_all 쿼리 사용
        boolean hasSearchCriteria = StringUtils.hasText(name) || StringUtils.hasText(email) ||
                StringUtils.hasText(phone) || StringUtils.hasText(role) ||
                StringUtils.hasText(content);

        if (!hasSearchCriteria) {
            // 모든 문서 검색
            NativeQuery searchQuery = new NativeQueryBuilder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withPageable(pageable)
                    .build();

            SearchHits<MemberDocument> searchHits = elasticsearchOperations.search(searchQuery, MemberDocument.class);

            List<MemberDocument> members = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .toList();

            return new PageImpl<>(members, pageable, searchHits.getTotalHits());
        }

        // 검색 조건에 따른 쿼리 추가
        if (StringUtils.hasText(name)) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("name").query(name)));
        }
        if (StringUtils.hasText(email)) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("email").query(email)));
        }
        if (StringUtils.hasText(phone)) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("phone").query(phone)));
        }
        if (StringUtils.hasText(role)) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("role").query(role)));
        }
        if (StringUtils.hasText(content)) {
            queryBuilder.must(QueryBuilders.multiMatch(m -> m
                    .query(content)
                    .fields("name", "email", "phone", "role")
            ));
        }

        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();

        SearchHits<MemberDocument> searchHits = elasticsearchOperations.search(searchQuery, MemberDocument.class);

        List<MemberDocument> members = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(members, pageable, searchHits.getTotalHits());
    }
}
