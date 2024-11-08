package home.project.repositoryForElasticsearch;

import home.project.domain.elasticsearch.OrdersDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersElasticsearchRepositoryCustom {
    Page<OrdersDocument> findOrders(String orderNum, String orderDate, String productNumber,
                                    String email, String content, Pageable pageable);
}