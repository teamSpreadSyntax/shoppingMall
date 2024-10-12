package home.project.repository;

import home.project.domain.Coupon;
import home.project.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepositoryCustom {
    Page<Event> findEvents(String name, String startDate, String endDate, Integer discountRate, String content, Pageable pageable);

}