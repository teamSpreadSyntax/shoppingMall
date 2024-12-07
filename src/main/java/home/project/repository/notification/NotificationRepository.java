package home.project.repository.notification;

import home.project.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findByMemberIdAndNotificationId(Long memberId, Long notificationId);
}
