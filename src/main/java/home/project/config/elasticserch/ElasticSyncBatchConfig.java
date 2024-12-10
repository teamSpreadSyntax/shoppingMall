package home.project.config.elasticserch;

import home.project.domain.elasticsearch.CouponDocument;
import home.project.domain.elasticsearch.MemberDocument;
import home.project.domain.elasticsearch.OrdersDocument;
import home.project.domain.elasticsearch.ProductDocument;
import home.project.domain.member.Member;
import home.project.domain.order.Orders;
import home.project.domain.product.Coupon;
import home.project.domain.product.Product;
import home.project.service.util.Converter;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ElasticSyncBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final ElasticsearchOperations elasticsearchOperations;
    private final Converter converter;

    @Bean
    public Job elasticSyncJob() {
        return new JobBuilder("elasticSyncJob", jobRepository)
                .start(memberStep())
                .next(productStep())
                .next(orderStep())
                .next(couponStep())
                .build();
    }

    @Bean
    public Step memberStep() {
        return new StepBuilder("memberStep", jobRepository)
                .<Member, MemberDocument>chunk(100, transactionManager)
                .reader(memberReader())
                .processor(converter::convertFromMemberToMemberDocument)
                .writer(memberWriter())
                .build();
    }

    @Bean
    public Step productStep() {
        return new StepBuilder("productStep", jobRepository)
                .<Product, ProductDocument>chunk(100, transactionManager)
                .reader(productReader())
                .processor(converter::convertFromProductToProductDocument)
                .writer(productWriter())
                .build();
    }

    @Bean
    public Step orderStep() {
        return new StepBuilder("orderStep", jobRepository)
                .<Orders, OrdersDocument>chunk(100, transactionManager)
                .reader(orderReader())
                .processor(converter::convertFromOrderToOrdersDocument)
                .writer(orderWriter())
                .build();
    }

    @Bean
    public Step couponStep() {
        return new StepBuilder("couponStep", jobRepository)
                .<Coupon, CouponDocument>chunk(100, transactionManager)
                .reader(couponReader())
                .processor(converter::convertFromCouponToCouponDocument)
                .writer(couponWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Member> memberReader() {
        return new JpaPagingItemReaderBuilder<Member>()
                .name("memberReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString("SELECT m FROM Member m")  // 컬렉션을 즉시 페치하지 않음
                .build();
    }

    @Bean
    public JpaPagingItemReader<Product> productReader() {
        return new JpaPagingItemReaderBuilder<Product>()
                .name("productReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category")  // 컬렉션을 즉시 페치하지 않음
                .build();
    }

    @Bean
    public JpaPagingItemReader<Orders> orderReader() {
        return new JpaPagingItemReaderBuilder<Orders>()
                .name("orderReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString("SELECT o FROM Orders o")  // 컬렉션을 즉시 페치하지 않음
                .build();
    }

    @Bean
    public JpaPagingItemReader<Coupon> couponReader() {
        return new JpaPagingItemReaderBuilder<Coupon>()
                .name("couponReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString("SELECT c FROM Coupon c")  // 컬렉션을 즉시 페치하지 않음
                .build();
    }

    @Bean
    public ItemWriter<MemberDocument> memberWriter() {
        return items -> {
            try {
                elasticsearchOperations.save(items);
                log.info("Saved {} members to Elasticsearch", items.size());
            } catch (Exception e) {
                log.error("Failed to save members to Elasticsearch", e);
                throw e;
            }
        };
    }

    @Bean
    public ItemWriter<ProductDocument> productWriter() {
        return items -> {
            try {
                elasticsearchOperations.save(items);
                log.info("Saved {} products to Elasticsearch", items.size());
            } catch (Exception e) {
                log.error("Failed to save products to Elasticsearch", e);
                throw e;
            }
        };
    }

    @Bean
    public ItemWriter<OrdersDocument> orderWriter() {
        return items -> {
            try {
                elasticsearchOperations.save(items);
                log.info("Saved {} orders to Elasticsearch", items.size());
            } catch (Exception e) {
                log.error("Failed to save orders to Elasticsearch", e);
                throw e;
            }
        };
    }

    @Bean
    public ItemWriter<CouponDocument> couponWriter() {
        return items -> {
            try {
                elasticsearchOperations.save(items);
                log.info("Saved {} coupons to Elasticsearch", items.size());
            } catch (Exception e) {
                log.error("Failed to save coupons to Elasticsearch", e);
                throw e;
            }
        };
    }
}
