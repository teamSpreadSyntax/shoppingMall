package home.project.repository.notification;

import home.project.domain.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByIdAndMemberId(Long notificationId, Long memberId);

    Page<Notification> findAllByMemberId(Pageable pageable, Long memberId);
}
