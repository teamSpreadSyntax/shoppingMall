package home.project.repository.promotion;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import home.project.domain.promotion.Event;
import home.project.domain.promotion.QEvent;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventRepositoryCustomImpl implements EventRepositoryCustom  {
    private final JPAQueryFactory queryFactory;
    private final QEvent event = QEvent.event;

    public EventRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Event> findEvents(String name, String startDate, String endDate, Integer discountRate, String content, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(name)) {
            builder.and(event.name.toLowerCase().contains(name.toLowerCase()));
        }
        if (StringUtils.hasText(startDate)) {
            LocalDateTime parsedStartDate = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
            builder.and(event.startDate.goe(parsedStartDate));
        }
        if (StringUtils.hasText(endDate)) {
            LocalDateTime parsedEndDate = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);
            builder.and(event.endDate.loe(parsedEndDate));
        }
        if (StringUtils.hasText(content)) {
            String[] keywords = content.toLowerCase().split("\\s+");
            for (String keyword : keywords) {
                builder.and(
                        event.name.toLowerCase().contains(keyword)
                                .or(event.startDate.stringValue().toLowerCase().contains(keyword))
                                .or(event.endDate.stringValue().toLowerCase().contains(keyword))
                );
            }
        }

        List<Event> results = queryFactory
                .selectFrom(event)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(event)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
