package home.project.service.product;

import home.project.domain.elasticsearch.ProductDocument;
import home.project.domain.member.MemberProduct;
import home.project.domain.product.Product;
import home.project.repository.member.MemberProductRepository;
import home.project.service.integration.IndexToElasticsearch;
import home.project.service.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.elasticsearch.core.query.Query;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductReindexService {

    private final MemberProductRepository memberProductRepository;
    private final Converter converter;
    private final IndexToElasticsearch indexToElasticsearch;
    private final ElasticsearchOperations elasticsearchOperations;

    @Transactional
    public void reindexAllProductsToES() {
        // Product ID 기준으로 한 번만 색인되도록 중복 제거
        Map<Long, MemberProduct> productToMemberMap = memberProductRepository.findAll().stream()
                .collect(Collectors.toMap(
                        mp -> mp.getProduct().getId(),
                        mp -> mp,
                        (existing, duplicate) -> existing // 중복 시 첫 번째만 사용
                ));

        for (MemberProduct mp : productToMemberMap.values()) {
            Product product = mp.getProduct();
            ProductDocument doc = converter.convertFromProductToProductDocument(product);
            doc.setMemberId(mp.getMember().getId()); // 첫 번째 member 기준
            indexToElasticsearch.indexDocumentToElasticsearch(doc, ProductDocument.class);
        }
    }

    public void deleteAllProductDocuments() {
        Query query = new CriteriaQuery(new Criteria("*").exists());
        elasticsearchOperations.delete(query, ProductDocument.class);
        System.out.println("🧹 전체 상품 문서 삭제 완료 (인덱스는 유지됨)");
    }


}
