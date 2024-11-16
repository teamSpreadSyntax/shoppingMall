package home.project.repositoryForElasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import home.project.domain.elasticsearch.OrdersDocument;
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

public class OrdersElasticsearchRepositoryCustomImpl implements OrdersElasticsearchRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;

    public OrdersElasticsearchRepositoryCustomImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public Page<OrdersDocument> findOrders(String orderNum, String orderDate, String productNumber,
                                           String email, String content, Pageable pageable) {

        BoolQuery.Builder queryBuilder = new BoolQuery.Builder();

        // 검색 조건이 하나도 없으면 match_all 쿼리 사용
        boolean hasSearchCriteria = StringUtils.hasText(orderNum) || StringUtils.hasText(orderDate) ||
                StringUtils.hasText(productNumber) || StringUtils.hasText(email) ||
                StringUtils.hasText(content);

        if (!hasSearchCriteria) {
            // 모든 문서 검색
            NativeQuery searchQuery = new NativeQueryBuilder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withPageable(pageable)
                    .build();

            SearchHits<OrdersDocument> searchHits = elasticsearchOperations.search(searchQuery, OrdersDocument.class);

            List<OrdersDocument> orders = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .toList();

            return new PageImpl<>(orders, pageable, searchHits.getTotalHits());
        }

        // 검색 조건에 따른 쿼리 추가
        if (StringUtils.hasText(orderNum)) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("orderNum").query(orderNum)));
        }
        if (StringUtils.hasText(orderDate)) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("orderDate").query(orderDate)));
        }
        if (StringUtils.hasText(productNumber)) {
            queryBuilder.must(QueryBuilders.nested(n -> n
                    .path("productOrders")
                    .query(q -> q
                            .match(m -> m
                                    .field("productOrders.productNum")
                                    .query(productNumber)))));
        }
        if (StringUtils.hasText(email)) {
            queryBuilder.must(QueryBuilders.nested(n -> n
                    .path("member")
                    .query(q -> q
                            .match(m -> m
                                    .field("member.email")
                                    .query(email)))));
        }
        if (StringUtils.hasText(content)) {
            queryBuilder.must(QueryBuilders.multiMatch(m -> m
                    .query(content)
                    .fields("orderNum", "member.email", "member.name",
                            "productOrders.productName", "shipping.deliveryAddress")
            ));
        }

        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();

        SearchHits<OrdersDocument> searchHits = elasticsearchOperations.search(searchQuery, OrdersDocument.class);

        List<OrdersDocument> orders = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(orders, pageable, searchHits.getTotalHits());
    }
}