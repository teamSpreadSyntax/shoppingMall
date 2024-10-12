package home.project.repository;

import home.project.domain.Coupon;
import home.project.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom  {

}
