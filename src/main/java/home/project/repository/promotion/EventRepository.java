package home.project.repository.promotion;

import home.project.domain.promotion.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom  {

}
