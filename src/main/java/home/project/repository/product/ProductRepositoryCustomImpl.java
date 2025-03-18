package home.project.repository.product;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import home.project.domain.delivery.DeliveryStatusType;
import home.project.domain.delivery.QShipping;
import home.project.domain.order.QOrders;
import home.project.domain.product.*;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;
    private final QCategory category = QCategory.category;
    private final QProductOrder productOrder = QProductOrder.productOrder;
    private final QOrders orders = QOrders.orders;
    private final QShipping shipping = QShipping.shipping;

    public ProductRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Product> findProducts(String brand, String categoryCode, String productName, String content, String colors, String sizes, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (brand != null && !brand.isEmpty()) {
            builder.and(product.brand.toLowerCase().like("%" + brand.toLowerCase() + "%"));
        }
        if (categoryCode != null && !categoryCode.isEmpty()) {
            builder.and(product.category.code.toLowerCase().like(categoryCode.toLowerCase() + "%"));
        }
        if (productName != null && !productName.isEmpty()) {
            builder.and(product.name.toLowerCase().like("%" + productName.toLowerCase() + "%"));
        }
        if (content != null && !content.isEmpty()) {
            builder.or(product.brand.lower().like("%" + content.toLowerCase() + "%"))
                    .or(product.category.code.lower().like("%" + content.toLowerCase() + "%"))
                    .or(product.name.lower().like("%" + content.toLowerCase() + "%"));
        }
        if (colors != null && !colors.isEmpty()) {
            builder.and(product.color.equalsIgnoreCase(colors)); // 단일 color 조건
        }
        if (sizes != null && !sizes.isEmpty()) {
            builder.and(product.size.equalsIgnoreCase(sizes)); // 단일 size 조건
        }

        List<Product> results = queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(product)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Product> findProducts(String brand, String category, String productName, String content, Pageable pageable) {
        // 누락된 파라미터를 null로 전달하여 기존 메서드 호출
        return findProducts(brand, category, productName, content, null, null, pageable);
    }

    @Override
    public Page<Product> findSoldProducts(String brand, String categoryCode, String productName, String content, String colors, String sizes, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (brand != null && !brand.isEmpty()) {
            builder.and(product.brand.toLowerCase().like("%" + brand.toLowerCase() + "%"));
        }
        if (categoryCode != null && !categoryCode.isEmpty()) {
            builder.and(product.category.code.toLowerCase().like(categoryCode.toLowerCase() + "%"));
        }
        if (productName != null && !productName.isEmpty()) {
            builder.and(product.name.toLowerCase().like("%" + productName.toLowerCase() + "%"));
        }
        if (content != null && !content.isEmpty()) {
            builder.or(product.brand.lower().like("%" + content.toLowerCase() + "%"))
                    .or(product.category.code.lower().like("%" + content.toLowerCase() + "%"))
                    .or(product.name.lower().like("%" + content.toLowerCase() + "%"));
        }
        if (colors != null && !colors.isEmpty()) {
            builder.and(product.color.equalsIgnoreCase(colors)); // 단일 color 조건
        }
        if (sizes != null && !sizes.isEmpty()) {
            builder.and(product.size.equalsIgnoreCase(sizes)); // 단일 size 조건
        }
        builder.and(product.productOrder.any().orders.shipping.deliveryStatus.eq(DeliveryStatusType.PURCHASE_CONFIRMED));

        List<Product> results = queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category).fetchJoin()
                .leftJoin(product.productOrder, productOrder).fetchJoin()
                .leftJoin(productOrder.orders, orders).fetchJoin()
                .leftJoin(orders.shipping, shipping).fetchJoin()
                .where(builder)
                .distinct()
                .orderBy(product.soldQuantity.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(product)
                .leftJoin(product.productOrder, productOrder)
                .leftJoin(productOrder.orders, orders)
                .leftJoin(orders.shipping, shipping)
                .where(builder)
                .distinct()
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    public Page<Product> findAllBySoldQuantity(Pageable pageable) {
        List<Product> results = queryFactory
                .selectFrom(product)
                .orderBy(product.soldQuantity.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(product)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Product> findAllByOrderByBrandAsc(Pageable pageable) {
        // 결과 리스트 쿼리
        List<Product> results = queryFactory
                .selectFrom(product)
                .where(product.id.in(
                        JPAExpressions.select(product.id.min())
                                .from(product)
                                .groupBy(product.brand) // 브랜드별 그룹화
                ))
                .orderBy(product.brand.asc()) // 브랜드 기준 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 브랜드 수 카운트
        long total = queryFactory
                .select(product.brand)
                .distinct()
                .from(product)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public List<Product> findAllByCategory(Category category) {
        return queryFactory
                .selectFrom(product)
                .where(product.category.id.eq(category.getId()))
                .fetch();
    }

    @Override
    public Page<Product> findTop20LatestProducts(Pageable pageable) {
        List<Product> results = queryFactory
                .selectFrom(product)
                .orderBy(product.createAt.desc())
                .offset(pageable.getOffset())
                .limit(Math.min(20L, pageable.getPageSize()))
                .fetch();

        long total = Math.min(20L, queryFactory
                .selectFrom(product)
                .fetchCount());

        return new PageImpl<>(results, pageable, total);
    }
}
