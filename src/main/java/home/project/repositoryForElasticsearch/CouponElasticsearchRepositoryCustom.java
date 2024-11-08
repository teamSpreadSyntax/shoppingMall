package home.project.repositoryForElasticsearch;

import home.project.domain.elasticsearch.CouponDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponElasticsearchRepositoryCustom {
    Page<CouponDocument> findCoupons(String name, Integer discountRate, String startDate,
                                     String endDate, String content, Pageable pageable);
}