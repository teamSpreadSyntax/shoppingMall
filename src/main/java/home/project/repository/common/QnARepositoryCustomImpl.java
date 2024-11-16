package home.project.repository.common;

import com.querydsl.jpa.impl.JPAQueryFactory;

import home.project.domain.member.QMember;
import home.project.domain.order.QCart;
import jakarta.persistence.EntityManager;

public class QnARepositoryCustomImpl implements QnARepositoryCustom  {
    private final JPAQueryFactory queryFactory;
    private final QCart cart = QCart.cart;
    private final QMember member = QMember.member;

    public QnARepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

}
