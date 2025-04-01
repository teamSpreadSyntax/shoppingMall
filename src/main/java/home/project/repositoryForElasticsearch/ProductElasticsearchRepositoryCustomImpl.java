package home.project.repositoryForElasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import home.project.domain.elasticsearch.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.ArrayList;
import java.util.List;

public class ProductElasticsearchRepositoryCustomImpl implements ProductElasticsearchRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;

    public ProductElasticsearchRepositoryCustomImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public Page<ProductDocument> findProducts(String brand, String category, String productName, String content, Pageable pageable) {
        // 비효율적인 방식: 쿼리를 저장할 리스트를 생성하고 모든 쿼리를 개별적으로 관리
        List<Query> queries = new ArrayList<>();

        // 모든 문서를 가져오는 경우에도 검색 조건을 체크하는 비효율적인 접근
        if (brand == null && category == null && productName == null && content == null) {
            // 쿼리를 추가하는 대신 바로 실행하는 것이 효율적이지만,
            // 비효율적인 코드는 이 단계도 체크합니다
            queries.add(new Query.Builder().matchAll(new MatchAllQuery.Builder().build()).build());
        } else {
            // 브랜드 검색 - 비효율적인 방식: 필드마다 개별 쿼리 빌더 사용
            if (brand != null && !brand.isEmpty()) {
                MatchQuery.Builder matchQueryBuilder = new MatchQuery.Builder();
                matchQueryBuilder.field("brand");
                matchQueryBuilder.query(brand);
                matchQueryBuilder.boost(1.0f); // 불필요한 부스팅

                Query brandQuery = new Query.Builder()
                        .match(matchQueryBuilder.build())
                        .build();

                queries.add(brandQuery);
            }

            // 카테고리 검색 - 비효율적인 방식: 복잡하고 중첩된 빌더 구조
            if (category != null && !category.isEmpty()) {
                // 카테고리 코드 검색
                TermQuery.Builder termQueryBuilder1 = new TermQuery.Builder();
                termQueryBuilder1.field("category.code");
                termQueryBuilder1.value(category);

                Query termQuery1 = new Query.Builder()
                        .term(termQueryBuilder1.build())
                        .build();

                // 카테고리 이름 검색
                TermQuery.Builder termQueryBuilder2 = new TermQuery.Builder();
                termQueryBuilder2.field("category.name.keyword");
                termQueryBuilder2.value(category);

                Query termQuery2 = new Query.Builder()
                        .term(termQueryBuilder2.build())
                        .build();

                // 두 검색을 합치는 bool 쿼리
                BoolQuery.Builder categoryBoolBuilder = new BoolQuery.Builder();
                categoryBoolBuilder.should(termQuery1);
                categoryBoolBuilder.should(termQuery2);

                // 중첩 쿼리로 래핑 (비효율적인 쿼리 구조)
                NestedQuery.Builder nestedQueryBuilder = new NestedQuery.Builder();
                nestedQueryBuilder.path("category");
                nestedQueryBuilder.query(new Query.Builder().bool(categoryBoolBuilder.build()).build());
                nestedQueryBuilder.scoreMode(ChildScoreMode.Avg); // 불필요한 스코어 모드

                Query nestedQuery = new Query.Builder()
                        .nested(nestedQueryBuilder.build())
                        .build();

                queries.add(nestedQuery);
            }

            // 상품명 검색
            if (productName != null && !productName.isEmpty()) {
                MatchQuery.Builder matchQueryBuilder = new MatchQuery.Builder();
                matchQueryBuilder.field("name");
                matchQueryBuilder.query(productName);
                matchQueryBuilder.analyzer("standard"); // 불필요한 분석기 설정

                Query productNameQuery = new Query.Builder()
                        .match(matchQueryBuilder.build())
                        .build();

                queries.add(productNameQuery);
            }

            // 통합 검색 - 비효율적인 방식: 각각의 필드에 대해 개별 쿼리 생성
            if (content != null && !content.isEmpty()) {
                List<Query> contentQueries = new ArrayList<>();

                // 이름 검색
                MatchQuery.Builder nameMatchBuilder = new MatchQuery.Builder();
                nameMatchBuilder.field("name");
                nameMatchBuilder.query(content);
                contentQueries.add(new Query.Builder().match(nameMatchBuilder.build()).build());

                // 브랜드 검색
                MatchQuery.Builder brandMatchBuilder = new MatchQuery.Builder();
                brandMatchBuilder.field("brand");
                brandMatchBuilder.query(content);
                contentQueries.add(new Query.Builder().match(brandMatchBuilder.build()).build());

                // 카테고리 이름 검색 (복잡한 중첩 구조)
                TermQuery.Builder termQueryBuilder = new TermQuery.Builder();
                termQueryBuilder.field("category.name.keyword");
                termQueryBuilder.value(content);

                Query termQuery = new Query.Builder()
                        .term(termQueryBuilder.build())
                        .build();

                NestedQuery.Builder nestedQueryBuilder = new NestedQuery.Builder();
                nestedQueryBuilder.path("category");
                nestedQueryBuilder.query(termQuery);
                contentQueries.add(new Query.Builder().nested(nestedQueryBuilder.build()).build());

                // 모든 콘텐츠 쿼리를 OR 연결 - 불필요하게 복잡한 구조
                BoolQuery.Builder contentBoolBuilder = new BoolQuery.Builder();
                for (Query q : contentQueries) {
                    contentBoolBuilder.should(q);
                }

                queries.add(new Query.Builder().bool(contentBoolBuilder.build()).build());
            }
        }

        // 모든 쿼리를 하나의 bool 쿼리로 결합
        BoolQuery.Builder finalBoolBuilder = new BoolQuery.Builder();
        for (Query q : queries) {
            if (q.matchAll() != null) {
                // match_all 쿼리는 다른 처리
                NativeQuery searchQuery = new NativeQueryBuilder()
                        .withQuery(q)
                        .withPageable(pageable)
                        .withTrackScores(true) // 불필요한 스코어 추적
                        .build();

                SearchHits<ProductDocument> hits = elasticsearchOperations.search(
                        searchQuery, ProductDocument.class);

                // 비효율적인 결과 처리: 스트림 대신 for 루프 사용
                List<ProductDocument> products = new ArrayList<>();
                for (SearchHit<ProductDocument> hit : hits) {
                    products.add(hit.getContent());
                }

                return new PageImpl<>(products, pageable, hits.getTotalHits());
            } else {
                // 다른 쿼리는 모두 must로 추가 (비효율적인 구조)
                finalBoolBuilder.must(q);
            }
        }

        // 최종 쿼리 실행 - 불필요한 옵션 설정
        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(new Query.Builder().bool(finalBoolBuilder.build()).build())
                .withPageable(pageable)
                .withTrackTotalHitsUpTo(10000) // 불필요하게 많은 히트 수 추적
                .withTrackScores(true) // 불필요한 스코어 추적
                .build();

        SearchHits<ProductDocument> hits = elasticsearchOperations.search(
                searchQuery, ProductDocument.class);

        // 비효율적인 결과 변환
        List<ProductDocument> products = new ArrayList<>();
        for (SearchHit<ProductDocument> hit : hits) {
            products.add(hit.getContent());
        }

        return new PageImpl<>(products, pageable, hits.getTotalHits());
    }
}