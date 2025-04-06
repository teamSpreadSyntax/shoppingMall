package home.project.service.product;

import home.project.domain.elasticsearch.ProductDocument;
import home.project.domain.member.MemberProduct;
import home.project.domain.product.Product;
import home.project.repository.member.MemberProductRepository;
import home.project.service.integration.IndexToElasticsearch;
import home.project.service.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductReindexService {

    private final MemberProductRepository memberProductRepository;
    private final Converter converter;
    private final IndexToElasticsearch indexToElasticsearch;

    @Transactional
    public void reindexAllProductsToES() {
        List<MemberProduct> all = memberProductRepository.findAll();

        for (MemberProduct mp : all) {
            Product product = mp.getProduct();
            ProductDocument doc = converter.convertFromProductToProductDocument(product);
            doc.setMemberId(mp.getMember().getId());
            indexToElasticsearch.indexDocumentToElasticsearch(doc, ProductDocument.class);
        }
    }
}
