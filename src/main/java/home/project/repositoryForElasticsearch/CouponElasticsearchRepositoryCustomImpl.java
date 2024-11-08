package home.project.repositoryForElasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import home.project.domain.elasticsearch.CouponDocument;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CouponElasticsearchRepositoryCustomImpl implements CouponElasticsearchRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<CouponDocument> findCoupons(String name, Integer discountRate, String startDate,
                                            String endDate, String content, Pageable pageable) {
        BoolQuery.Builder queryBuilder = new BoolQuery.Builder();

        boolean hasSearchCriteria = StringUtils.hasText(name) || discountRate != null ||
                StringUtils.hasText(startDate) || StringUtils.hasText(endDate) ||
                StringUtils.hasText(content);

        if (!hasSearchCriteria) {
            NativeQuery searchQuery = new NativeQueryBuilder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withPageable(pageable)
                    .build();

            SearchHits<CouponDocument> searchHits = elasticsearchOperations.search(searchQuery, CouponDocument.class);

            List<CouponDocument> coupons = searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());

            return new PageImpl<>(coupons, pageable, searchHits.getTotalHits());
        }

        if (StringUtils.hasText(name)) {
            queryBuilder.must(QueryBuilders.match(m -> m.field("name").query(name)));
        }
        if (discountRate != null) {
            queryBuilder.must(QueryBuilders.term(t -> t.field("discountRate").value(discountRate)));
        }
        if (StringUtils.hasText(startDate)) {
            queryBuilder.must(QueryBuilders.range(r -> r.field("startDate").gte(JsonData.of(startDate))));
        }
        if (StringUtils.hasText(endDate)) {
            queryBuilder.must(QueryBuilders.range(r -> r.field("endDate").lte(JsonData.of(endDate))));
        }
        if (StringUtils.hasText(content)) {
            queryBuilder.must(QueryBuilders.multiMatch(m -> m
                    .query(content)
                    .fields("name", "assignBy", "productCoupons.productName", "memberCoupons.name")
            ));
        }

        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();

        SearchHits<CouponDocument> searchHits = elasticsearchOperations.search(searchQuery, CouponDocument.class);

        List<CouponDocument> coupons = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(coupons, pageable, searchHits.getTotalHits());
    }
}