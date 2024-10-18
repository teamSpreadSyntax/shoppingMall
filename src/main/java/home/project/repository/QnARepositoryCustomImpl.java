package home.project.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import home.project.domain.Cart;
import home.project.domain.QCart;
import home.project.domain.QMember;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class QnARepositoryCustomImpl implements QnARepositoryCustom  {
    private final JPAQueryFactory queryFactory;
    private final QCart cart = QCart.cart;
    private final QMember member = QMember.member;

    public QnARepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

}
