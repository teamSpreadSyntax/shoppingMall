/*
package home.project.service;

import home.project.domain.Notification;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class NotificationServiceImpl extends NotificationService{

    private final NotificationRepository notificationRepository;
    private final Converter converter;

    // 공지사항 생성
    public NotificationResponse createNotification(CreateOrderRequestDTO createOrderRequestDTO) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        Notification savedNotification = notificationRepository.save(notification);
        return converter.notificationResponse(savedNotification);
    }

    // 모든 공지사항 조회
    public List<NotificationResponse> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        return converter.convertNotificationsToResponse(notifications);
    }

    // 특정 공지사항 조회
    public Optional<NotificationResponse> getNotificationById(Long id) {
        return notificationRepository.findById(id).map(converter::notificationResponse);
    }

    // 특정 공지사항 삭제
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}*/
