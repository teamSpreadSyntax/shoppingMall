package home.project.service.notification;

import home.project.domain.member.Member;
import home.project.domain.notification.Notification;
import home.project.domain.notification.NotificationType;
import home.project.dto.requestDTO.CreateNotificationRequestDTO;
import home.project.dto.responseDTO.NotificationDetailResponse;
import home.project.dto.responseDTO.NotificationResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.notification.NotificationRepository;
import home.project.service.member.MemberService;
import home.project.service.util.Converter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private MemberService memberService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private Converter converter;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Member testMember;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        // 인증 객체 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@test.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 테스트 데이터 초기화
        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@test.com");

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setMember(testMember);
        testNotification.setNotificationType(NotificationType.Coupon);
        testNotification.setDescription("Test Notification");
        testNotification.setRead(false);
    }

    @Nested
    @DisplayName("알림 생성")
    class CreateNotificationTest {

        @Test
        @DisplayName("일반 알림 생성 성공")
        void createNotificationSuccess() {
            // given
            CreateNotificationRequestDTO requestDTO = new CreateNotificationRequestDTO();
            requestDTO.setNotificationType(NotificationType.Coupon);
            requestDTO.setDescription("New Notification");

            NotificationResponse expectedResponse = new NotificationResponse(
                    1L,
                    1L,
                    NotificationType.Coupon,
                    "New Notification",
                    false,
                    LocalDateTime.now() // createdAt
            );

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
            when(converter.convertFromNotificationToNotificationResponse(any(Notification.class)))
                    .thenReturn(expectedResponse);

            // when
            NotificationResponse response = notificationService.createNotification(requestDTO);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getNotificationType()).isEqualTo(NotificationType.Coupon);
            assertThat(response.getDescription()).isEqualTo("New Notification");

            verify(notificationRepository).save(any(Notification.class));
        }

        @Test
        @DisplayName("일반 알림 생성 실패 - 회원 없음")
        void createNotificationFailMemberNotFound() {
            // given
            CreateNotificationRequestDTO requestDTO = new CreateNotificationRequestDTO();
            requestDTO.setNotificationType(NotificationType.Coupon);
            requestDTO.setDescription("New Notification");

            when(memberService.findByEmail(anyString())).thenThrow(new IdNotFoundException("회원이 존재하지 않습니다."));

            // when & then
            assertThatThrownBy(() -> notificationService.createNotification(requestDTO))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("회원이 존재하지 않습니다.");

            verify(notificationRepository, never()).save(any(Notification.class));
        }
    }

    @Nested
    @DisplayName("쿠폰 알림 생성")
    class CreateCouponNotificationTest {

        @Test
        @DisplayName("쿠폰 알림 생성 성공")
        void createCouponNotificationSuccess() {
            // given
            String description = "Coupon Notification";

            NotificationResponse expectedResponse = new NotificationResponse(
                    1L,
                    1L,
                    NotificationType.Coupon,
                    "Coupon Notification",
                    false,
                    LocalDateTime.now() // createdAt
            );

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
            when(converter.convertFromNotificationToNotificationResponse(any(Notification.class)))
                    .thenReturn(expectedResponse);

            // when
            NotificationResponse response = notificationService.createCouponNotification(description);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getNotificationType()).isEqualTo(NotificationType.Coupon);
            assertThat(response.getDescription()).isEqualTo(description);

            verify(notificationRepository).save(any(Notification.class));
        }

        @Test
        @DisplayName("쿠폰 알림 생성 실패 - 회원 없음")
        void createCouponNotificationFailMemberNotFound() {
            // given
            String description = "Coupon Notification";

            when(memberService.findByEmail(anyString())).thenThrow(new IdNotFoundException("회원이 존재하지 않습니다."));

            // when & then
            assertThatThrownBy(() -> notificationService.createCouponNotification(description))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("회원이 존재하지 않습니다.");

            verify(notificationRepository, never()).save(any(Notification.class));
        }
    }

    @Nested
    @DisplayName("알림 읽기")
    class ReadNotificationTest {

        @Test
        @DisplayName("알림 읽기 성공")
        void readNotificationSuccess() {
            // given
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(notificationRepository.findByIdAndMemberId(anyLong(), anyLong()))
                    .thenReturn(Optional.of(testNotification));

            // when
            String email = notificationService.readNotification(1L);

            // then
            assertThat(email).isEqualTo(testMember.getEmail());
            assertThat(testNotification.isRead()).isTrue();

            verify(notificationRepository).findByIdAndMemberId(anyLong(), anyLong());
            verify(notificationRepository).save(any(Notification.class));
        }

        @Test
        @DisplayName("알림 읽기 실패 - 알림 없음")
        void readNotificationFailNotFound() {
            // given
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(notificationRepository.findByIdAndMemberId(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationService.readNotification(1L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("알림을 찾을 수 없습니다.");

            verify(notificationRepository).findByIdAndMemberId(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("회원별 알림 조회")
    class FindAllByMemberIdTest {

        @Test
        @DisplayName("회원별 알림 조회 성공")
        void findAllByMemberIdSuccess() {
            // given
            Page<Notification> pagedNotifications = new PageImpl<>(Collections.singletonList(testNotification));
            Page<NotificationResponse> expectedResponses = new PageImpl<>(
                    Collections.singletonList(new NotificationResponse(
                            1L,
                            1L,
                            NotificationType.Coupon,
                            "New Notification",
                            false,
                            LocalDateTime.now() // createdAt
                    ))
            );

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(notificationRepository.findAllByMemberId(any(Pageable.class), anyLong()))
                    .thenReturn(pagedNotifications);
            when(converter.convertFromPagedNotificationsToPagedNotificationsResponse(any(Page.class)))
                    .thenReturn(expectedResponses);

            // when
            Page<NotificationResponse> responses = notificationService.findAllByMemberId(Pageable.unpaged());

            // then
            assertThat(responses).isNotNull();
            assertThat(responses.getContent()).hasSize(1);
            assertThat(responses.getContent().get(0).getNotificationType()).isEqualTo(NotificationType.Coupon);

            verify(notificationRepository).findAllByMemberId(any(Pageable.class), anyLong());
        }

        @Test
        @DisplayName("회원별 알림 조회 실패 - 회원 없음")
        void findAllByMemberIdFailMemberNotFound() {
            // given
            when(memberService.findByEmail(anyString())).thenThrow(new IdNotFoundException("회원이 존재하지 않습니다."));

            // when & then
            assertThatThrownBy(() -> notificationService.findAllByMemberId(Pageable.unpaged()))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("회원이 존재하지 않습니다.");

            verify(notificationRepository, never()).findAllByMemberId(any(Pageable.class), anyLong());
        }
    }

    @Nested
    @DisplayName("알림 삭제")
    class DeleteNotificationTest {

        @Test
        @DisplayName("알림 삭제 성공")
        void deleteNotificationSuccess() {
            // when
            notificationService.deleteById(1L);

            // then
            verify(notificationRepository).deleteById(anyLong());
        }

        @Test
        @DisplayName("알림 삭제 실패 - 알림 없음")
        void deleteNotificationFailNotFound() {
            // given
            doThrow(new IdNotFoundException("알림이 존재하지 않습니다."))
                    .when(notificationRepository).deleteById(anyLong());

            // when & then
            assertThatThrownBy(() -> notificationService.deleteById(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("알림이 존재하지 않습니다.");
        }
    }
}
