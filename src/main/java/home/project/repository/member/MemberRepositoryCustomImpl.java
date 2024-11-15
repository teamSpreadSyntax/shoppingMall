package home.project.repository.member;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import home.project.domain.member.Member;
import home.project.domain.member.QMember;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

public class MemberRepositoryCustomImpl implements MemberRepositoryCustom  {
    private final JPAQueryFactory queryFactory;
    private final QMember member = QMember.member;

    public MemberRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Member> findMembers(String name, String email, String phone, String role, String content, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(name)) {
            builder.and(member.name.toLowerCase().contains(name.toLowerCase()));
        }
        if (StringUtils.hasText(email)) {
            builder.and(member.email.toLowerCase().contains(email.toLowerCase()));
        }
        if (StringUtils.hasText(phone)) {
            builder.and(member.phone.toLowerCase().contains(phone.toLowerCase()));
        }
        if (StringUtils.hasText(role)) {
            builder.and(member.role.stringValue().toLowerCase().contains(role.toLowerCase()));
        }
        if (StringUtils.hasText(content)) {
            builder.and(member.name.toLowerCase().concat(" ")
                    .concat(member.email.toLowerCase()).concat(" ")
                    .concat(member.phone.toLowerCase()).concat(" ")
                    .concat(member.role.stringValue().toLowerCase())
                    .contains(content.toLowerCase()));
        }

        List<Member> results = queryFactory
                .selectFrom(member)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(member)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
