package home.project.service;

import home.project.domain.Notification;
import home.project.domain.QnA;
import home.project.dto.requestDTO.CreateNotificationRequestDTO;
import home.project.dto.responseDTO.NotificationDetailResponse;
import home.project.dto.responseDTO.NotificationResponse;
import home.project.dto.responseDTO.QnADetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationService {
     NotificationResponse createNotification(CreateNotificationRequestDTO createNotificationRequestDTO);

     Notification findById(Long notificationId);

    Page<NotificationResponse> findAllNotifications(Pageable pageable);

    NotificationDetailResponse findByIdReturnNotificationDetailResponse(Long notificationId);

    void deleteById(Long id);
}
