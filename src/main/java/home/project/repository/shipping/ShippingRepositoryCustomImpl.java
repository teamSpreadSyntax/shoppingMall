package home.project.repository.shipping;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import home.project.domain.delivery.QShipping;
import home.project.domain.delivery.Shipping;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShippingRepositoryCustomImpl implements ShippingRepositoryCustom  {
    private final JPAQueryFactory queryFactory;
    private final QShipping shipping = QShipping.shipping;

    public ShippingRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Shipping> findShippings(String deliveryNum, String orderDate, String orderNum, String email, String content, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(deliveryNum)) {
            builder.and(shipping.deliveryNum.toLowerCase().contains(deliveryNum.toLowerCase()));
        }
        if (StringUtils.hasText(orderDate)) {
            LocalDateTime parsedStartDate = LocalDateTime.parse(orderDate, DateTimeFormatter.ISO_DATE_TIME);
            builder.and(shipping.orders.orderDate.goe(parsedStartDate));
        }
        if (StringUtils.hasText(orderNum)) {
            builder.and(shipping.orders.orderNum.toLowerCase().contains(orderNum.toLowerCase()));
        }
        if (StringUtils.hasText(email)) {
            builder.and(shipping.orders.member.email.toLowerCase().contains(email.toLowerCase()));
        }
        if (StringUtils.hasText(content)) {
            String[] keywords = content.toLowerCase().split("\\s+");
            for (String keyword : keywords) {
                builder.and(
                        shipping.deliveryNum.toLowerCase().contains(keyword)
                                .or(shipping.orders.orderDate.stringValue().toLowerCase().contains(keyword))
                                .or(shipping.orders.orderNum.toLowerCase().contains(keyword))
                                .or(shipping.orders.member.email.toLowerCase().contains(keyword))
                );
            }
        }

        List<Shipping> results = queryFactory
                .selectFrom(shipping)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(shipping)
                .where(builder)
                .fetchCount();//fetch().size()

        return new PageImpl<>(results, pageable, total);
    }
}
