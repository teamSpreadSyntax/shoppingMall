package home.project.repository.order;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import home.project.domain.member.QMember;
import home.project.domain.order.Orders;
import home.project.domain.order.QOrders;
import home.project.domain.product.QProduct;
import home.project.domain.product.QProductOrder;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QOrders orders = QOrders.orders;
    private final QProductOrder productOrder = QProductOrder.productOrder;
    private final QProduct product = QProduct.product;
    private final QMember member = QMember.member;

    public OrderRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Orders> findOrders(String orderNum, String orderDate, String productNum, String email, String content, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // 주문 번호 필터
        if (StringUtils.hasText(orderNum)) {
            builder.and(orders.orderNum.toLowerCase().contains(orderNum.toLowerCase()));
        }

        // 주문 날짜 필터 (orderDate를 LocalDateTime으로 파싱)
        if (StringUtils.hasText(orderDate)) {
            LocalDateTime parsedOrderDate = LocalDateTime.parse(orderDate, DateTimeFormatter.ISO_DATE_TIME);
            builder.and(orders.orderDate.goe(parsedOrderDate));
        }

        // 상품 번호 필터
        if (StringUtils.hasText(productNum)) {
            builder.and(productOrder.product.productNum.eq(productNum));
        }

        // 이메일 필터
        if (StringUtils.hasText(email)) {
            builder.and(orders.member.email.toLowerCase().contains(email.toLowerCase()));
        }

        // content 필터 (다중 키워드 검색)
        if (StringUtils.hasText(content)) {
            String[] keywords = content.toLowerCase().split("\\s+"); // 공백 기준으로 키워드 분리
            for (String keyword : keywords) {
                builder.and(
                        orders.orderNum.toLowerCase().contains(keyword)
                                .or(orders.orderDate.stringValue().toLowerCase().contains(keyword))
                                .or(productOrder.product.productNum.toLowerCase().contains(keyword))
                                .or(orders.member.email.toLowerCase().contains(keyword))
                );
            }
        }

        // 검색 조건이 없을 경우 기본 조건 추가 (모든 주문 조회)
        if (!builder.hasValue()) {
            builder.and(orders.isNotNull());  // 기본적으로 모든 주문을 조회하는 조건
        }

        // 쿼리 실행 (페이징 적용)
        List<Orders> results = queryFactory
                .selectFrom(orders)
                .leftJoin(orders.member, member)
                .leftJoin(orders.productOrders, productOrder)
                .leftJoin(productOrder.product, product)
                .where(builder)
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 조회
        long total = queryFactory
                .selectFrom(orders)
                .leftJoin(orders.member, member)
                .leftJoin(orders.productOrders, productOrder)
                .leftJoin(productOrder.product, product)
                .where(builder)
                .distinct()
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}