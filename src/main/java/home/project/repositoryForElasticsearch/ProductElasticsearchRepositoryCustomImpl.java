package home.project.repositoryForElasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import home.project.domain.elasticsearch.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

public class ProductElasticsearchRepositoryCustomImpl implements ProductElasticsearchRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;

    public ProductElasticsearchRepositoryCustomImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public Page<ProductDocument> findProducts(String brand, String category, String productName, String content, Pageable pageable) {
        BoolQuery.Builder queryBuilder = new BoolQuery.Builder();

        // 검색 조건이 하나도 없으면 match_all 쿼리 사용
        boolean hasSearchCriteria = brand != null || category != null || productName != null || content != null;

        if (!hasSearchCriteria) {
            return findAllProducts(pageable);
        }

        // 브랜드 검색 - 한글/영문 모두 검색
        if (brand != null && !brand.isEmpty()) {
            queryBuilder.must(q -> q.bool(b -> b
                    .should(s -> s.match(m -> m.field("brand").query(brand)))
                    .should(s -> s.match(m -> m.field("brand.eng").query(brand)))
                    .minimumShouldMatch("1")
            ));
        }

        // 카테고리 검색
        if (category != null && !category.isEmpty()) {
            queryBuilder.must(q -> q.bool(b -> b
                    .should(s -> s.match(m -> m.field("categoryCode").query(category)))
                    .should(s -> s.match(m -> m.field("category.name").query(category)))
                    .should(s -> s.match(m -> m.field("category.name.eng").query(category)))
                    .minimumShouldMatch("1")
            ));
        }

        // 상품명 검색 - 한글/영문 모두 검색
        if (productName != null && !productName.isEmpty()) {
            queryBuilder.must(q -> q.bool(b -> b
                    .should(s -> s.match(m -> m.field("name").query(productName)))
                    .should(s -> s.match(m -> m.field("name.eng").query(productName)))
                    .minimumShouldMatch("1")
            ));
        }

        // 통합 검색 - 모든 필드에서 검색
        if (content != null && !content.isEmpty()) {
            queryBuilder.must(q -> q.bool(b -> b
                    .should(s -> s.match(m -> m.field("name").query(content).boost(2.0f)))
                    .should(s -> s.match(m -> m.field("name.eng").query(content).boost(2.0f)))
                    .should(s -> s.match(m -> m.field("brand").query(content)))
                    .should(s -> s.match(m -> m.field("brand.eng").query(content)))
                    .should(s -> s.match(m -> m.field("categoryCode").query(content)))
                    .should(s -> s.match(m -> m.field("description").query(content)))
                    .minimumShouldMatch("1")
            ));
        }

        return executeSearch(queryBuilder, pageable);
    }

    private Page<ProductDocument> findAllProducts(Pageable pageable) {
        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(q -> q.matchAll(m -> m))
                .withPageable(pageable)
                .build();

        return executeSearch(searchQuery);
    }

    private Page<ProductDocument> executeSearch(BoolQuery.Builder queryBuilder, Pageable pageable) {
        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();

        return executeSearch(searchQuery);
    }

    private Page<ProductDocument> executeSearch(NativeQuery searchQuery) {
        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(
                searchQuery,
                ProductDocument.class
        );

        List<ProductDocument> products = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(products, searchQuery.getPageable(), searchHits.getTotalHits());
    }
}