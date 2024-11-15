package home.project.repository.order;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import home.project.domain.member.QMember;
import home.project.domain.order.Cart;
import home.project.domain.order.QCart;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class CartRepositoryCustomImpl implements CartRepositoryCustom  {
    private final JPAQueryFactory queryFactory;
    private final QCart cart = QCart.cart;
    private final QMember member = QMember.member;

    public CartRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public Page<Cart> findAllByMemberId(Long memberId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // memberId로 필터링
        if (memberId != null) {
            builder.and(cart.member.id.eq(memberId));
        }

        // 페이징을 적용한 쿼리 실행
        List<Cart> results = queryFactory
                .selectFrom(cart)
                .orderBy(cart.id.asc())
                .leftJoin(cart.member, member).fetchJoin() // 회원 정보와 함께 가져옴
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 조회
        long total = queryFactory
                .selectFrom(cart)
                .where(builder)
                .fetchCount();

        // PageImpl 객체로 결과 반환
        return new PageImpl<>(results, pageable, total);
    }
}
