package home.project.repository.promotion;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import home.project.domain.product.Coupon;
import home.project.domain.product.QCoupon;
import home.project.domain.product.QMemberCoupon;
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
    private final QMemberCoupon memberCoupon = QMemberCoupon.memberCoupon;

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

    @Override
    public Page<Coupon> findAllByMemberId(Long memberId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // 회원 ID 필터링
        if (memberId != null) {
            builder.and(memberCoupon.member.id.eq(memberId));
        }

        // 페이징 쿼리 실행
        List<Coupon> results = queryFactory
                .selectFrom(coupon)
                .join(coupon.memberCoupons, memberCoupon).fetchJoin() // MemberCoupon 연관관계 조인
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 조회
        long total = queryFactory
                .selectFrom(coupon)
                .join(coupon.memberCoupons, memberCoupon)
                .where(builder)
                .fetchCount();

        // PageImpl 객체 반환
        return new PageImpl<>(results, pageable, total);
    }

}
