package home.project.service;

import home.project.domain.Member;
import home.project.domain.Notification;
import home.project.dto.requestDTO.CreateNotificationRequestDTO;
import home.project.dto.responseDTO.NotificationDetailResponse;
import home.project.dto.responseDTO.NotificationResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final MemberService memberService;
    private final NotificationRepository notificationRepository;
    private final Converter converter;

    @Override
    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequestDTO createNotificationRequestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Notification notification = new Notification();
        notification.setMember(member);
        notification.setSubject(createNotificationRequestDTO.getSubject());
        notification.setDescription(createNotificationRequestDTO.getDescription());
        notificationRepository.save(notification);
        return converter.convertFromNotificationToNotificationResponse(notification);
    }

    @Override
    public Notification findById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IdNotFoundException(notificationId + "(으)로 등록된 공지사항이 없습니다."));
    }

    @Override
    public Page<NotificationResponse> findAllNotifications(Pageable pageable) {
        Page<Notification> pagedNotifications = notificationRepository.findAll(pageable);
        return converter.convertFromPagedNotificationsToPagedNotificationsResponse(pagedNotifications);
    }

    @Override
    public NotificationDetailResponse findByIdReturnNotificationDetailResponse(Long notificationId) {
        return converter.convertFromNotificationToNotificationDetailResponse(findById(notificationId));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }
}
