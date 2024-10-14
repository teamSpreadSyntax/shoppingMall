package home.project.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import home.project.domain.Coupon;
import home.project.domain.QCoupon;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CouponRepositoryCustomImpl implements CouponRepositoryCustom  {
    private final JPAQueryFactory queryFactory;
    private final QCoupon coupon = QCoupon.coupon;

    public CouponRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Coupon> findCoupons(String name, String startDate, String endDate, String assignBy, String content, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(name)) {
            builder.and(coupon.name.toLowerCase().contains(name.toLowerCase()));
        }
        if (StringUtils.hasText(startDate)) {
            LocalDateTime parsedStartDate = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
            builder.and(coupon.startDate.goe(parsedStartDate));
        }
        if (StringUtils.hasText(endDate)) {
            LocalDateTime parsedEndDate = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);
            builder.and(coupon.endDate.loe(parsedEndDate));
        }
        if (StringUtils.hasText(assignBy)) {
            builder.and(coupon.assignBy.toLowerCase().contains(assignBy.toLowerCase()));
        }
        if (StringUtils.hasText(content)) {
            String[] keywords = content.toLowerCase().split("\\s+");
            for (String keyword : keywords) {
                builder.and(
                        coupon.name.toLowerCase().contains(keyword)
                                .or(coupon.assignBy.toLowerCase().contains(keyword))
                                .or(coupon.startDate.stringValue().toLowerCase().contains(keyword))
                                .or(coupon.endDate.stringValue().toLowerCase().contains(keyword))
                );
            }
        }

        List<Coupon> results = queryFactory
                .selectFrom(coupon)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(coupon)
                .where(builder)
                .fetchCount();//fetch().size()

        return new PageImpl<>(results, pageable, total);
    }
}
