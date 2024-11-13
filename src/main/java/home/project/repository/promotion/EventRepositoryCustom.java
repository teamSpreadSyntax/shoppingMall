package home.project.repository.promotion;

import home.project.domain.promotion.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepositoryCustom {
    Page<Event> findEvents(String name, String startDate, String endDate, Integer discountRate, String content, Pageable pageable);

}