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

        // 개별 조건
        if (brand != null && !brand.isEmpty()) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("brand").query(brand)));
        }
        if (category != null && !category.isEmpty()) {
            BoolQuery.Builder categoryQuery = new BoolQuery.Builder();

            // category.code로 정확히 검색
            categoryQuery.should(QueryBuilders.nested(n -> n
                    .path("category")
                    .query(q -> q
                            .term(t -> t
                                    .field("category.code")
                                    .value(category)
                            )
                    )
            ));

            // category.name.keyword로 정확히 검색 (수정됨)
            categoryQuery.should(QueryBuilders.nested(n -> n
                    .path("category")
                    .query(q -> q
                            .term(t -> t
                                    .field("category.name.keyword")
                                    .value(category) // 'content'에서 'category'로 수정
                            )
                    )
            ));

            queryBuilder.must(categoryQuery.build()._toQuery());
        }
        if (productName != null && !productName.isEmpty()) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("name").query(productName)));
        }

        // content 조건: 이름, 브랜드, 카테고리에서 검색
        if (content != null && !content.isEmpty()) {
            BoolQuery.Builder contentQuery = new BoolQuery.Builder()
                    .should(QueryBuilders.match(m -> m
                            .field("name")
                            .query(content)
                    ))
                    .should(QueryBuilders.match(m -> m
                            .field("brand")
                            .query(content)
                    ))
                    .should(QueryBuilders.nested(n -> n
                            .path("category")
                            .query(q -> q
                                    .term(t -> t
                                            .field("category.name.keyword")
                                            .value(content)
                                    )
                            )
                    ));
            queryBuilder.should(contentQuery.build()._toQuery()); // must -> should 유지
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
