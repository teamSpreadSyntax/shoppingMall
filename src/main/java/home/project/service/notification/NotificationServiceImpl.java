package home.project.service.notification;

import home.project.domain.notification.NotificationType;
import home.project.domain.member.Member;
import home.project.domain.notification.Notification;
import home.project.dto.requestDTO.CreateNotificationRequestDTO;
import home.project.dto.responseDTO.NotificationDetailResponse;
import home.project.dto.responseDTO.NotificationResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.notification.NotificationRepository;
import home.project.service.member.MemberService;
import home.project.service.util.Converter;
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
        notification.setNotificationType(createNotificationRequestDTO.getNotificationType());
        notification.setDescription(createNotificationRequestDTO.getDescription());
        notification.setRead(false);
        notificationRepository.save(notification);
        return converter.convertFromNotificationToNotificationResponse(notification);
    }

    @Override
    @Transactional
    public NotificationResponse createCouponNotification(String description) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Notification notification = new Notification();
        notification.setMember(member);
        notification.setNotificationType(NotificationType.Coupon);
        notification.setDescription(description);
        notification.setRead(false);
        notificationRepository.save(notification);

        return converter.convertFromNotificationToNotificationResponse(notification);
    }

    @Override
    @Transactional
    public String readNotification(Long notificationId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);
        Long memberId = member.getId();

        Notification notification = notificationRepository.findByIdAndMemberId(memberId, notificationId);
        notification.setRead(true);
        notificationRepository.save(notification);
        return email;
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
