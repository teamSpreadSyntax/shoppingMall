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
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHit;

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
            // 모든 문서 검색
            NativeQuery searchQuery = new NativeQueryBuilder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withPageable(pageable)
                    .build();

            SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(searchQuery, ProductDocument.class);

            List<ProductDocument> products = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .toList();

            return new PageImpl<>(products, pageable, searchHits.getTotalHits());
        }

        // 기존 검색 조건 로직
        if (brand != null && !brand.isEmpty()) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("brand").query(brand)));
        }
        if (category != null && !category.isEmpty()) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("categoryCode").query(category)));
        }
        if (productName != null && !productName.isEmpty()) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("name").query(productName)));
        }
        if (content != null && !content.isEmpty()) {
            queryBuilder.must(QueryBuilders.multiMatch(m -> m
                    .query(content)
                    .fields("brand", "name", "categoryCode")
            ));
        }

        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();

        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(searchQuery, ProductDocument.class);

        List<ProductDocument> products = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(products, pageable, searchHits.getTotalHits());
    }
}