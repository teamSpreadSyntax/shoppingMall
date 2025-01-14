package home.project.service.promotion;

import home.project.domain.product.Coupon;
import home.project.domain.promotion.Event;
import home.project.dto.requestDTO.CreateEventRequestDTO;
import home.project.dto.responseDTO.EventResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.promotion.EventRepository;
import home.project.repository.promotion.EventCouponRepository;
import home.project.service.util.Converter;
import home.project.service.file.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventCouponRepository eventCouponRepository;

    @Mock
    private CouponService couponService;

    @Mock
    private Converter converter;

    @Mock
    private FileService fileService;

    @InjectMocks
    private EventServiceImpl eventService;

    private CreateEventRequestDTO createEventRequestDTO;
    private Event testEvent;
    private Coupon testCoupon;

    @BeforeEach
    void setUp() {
        createEventRequestDTO = new CreateEventRequestDTO();
        createEventRequestDTO.setName("테스트 이벤트");
        createEventRequestDTO.setStartDate(LocalDateTime.of(2025, 1, 1, 0, 0));
        createEventRequestDTO.setEndDate(LocalDateTime.of(2025, 1, 31, 23, 59));
        createEventRequestDTO.setCouponId(1L);

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setName("테스트 이벤트");
        testEvent.setStartDate(LocalDateTime.of(2025, 1, 1, 0, 0));
        testEvent.setEndDate(LocalDateTime.of(2025, 1, 31, 23, 59));

        testCoupon = new Coupon();
        testCoupon.setId(1L);
        testCoupon.setName("테스트 쿠폰");
    }

    @Nested
    @DisplayName("이벤트 생성 테스트")
    class CreateEventTests {

        @Test
        @DisplayName("이벤트 수정 성공")
        void updateEventSuccess() {
            when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
            when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

            CreateEventRequestDTO updatedRequest = new CreateEventRequestDTO();
            updatedRequest.setName("Updated Event Name");
            updatedRequest.setStartDate(LocalDateTime.of(2025, 2, 1, 0, 0));  // 변경된 시작 날짜
            updatedRequest.setEndDate(LocalDateTime.of(2025, 2, 28, 23, 59));  // 변경된 종료 날짜
            updatedRequest.setCouponId(1L);

            when(converter.convertFromEventToEventResponse(any(Event.class)))
                    .thenReturn(new EventResponse(1L, "Updated Event Name", Collections.emptyList(),
                            LocalDateTime.of(2025, 2, 1, 0, 0),
                            LocalDateTime.of(2025, 2, 28, 23, 59)));

            EventResponse response = eventService.updateEvent(1L, updatedRequest, null, Collections.emptyList());

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Updated Event Name");
            assertThat(response.getStartDate()).isEqualTo(LocalDateTime.of(2025, 2, 1, 0, 0));
            assertThat(response.getEndDate()).isEqualTo(LocalDateTime.of(2025, 2, 28, 23, 59));
            verify(eventRepository).save(any(Event.class));
        }

        @Test
        @DisplayName("이벤트 생성 실패 - 쿠폰 찾을 수 없음")
        void createEventFailCouponNotFound() {
            when(couponService.findById(1L)).thenThrow(new IdNotFoundException("쿠폰을 찾을 수 없습니다"));

            assertThatThrownBy(() -> eventService.join(createEventRequestDTO, null, Collections.emptyList()))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("쿠폰을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("이벤트 수정 테스트")
    class UpdateEventTests {

        @Test
        @DisplayName("이벤트 수정 성공")
        void updateEventSuccess() {
            createEventRequestDTO.setName("Updated Event Name");
            createEventRequestDTO.setStartDate(LocalDateTime.of(2025, 2, 1, 0, 0));
            createEventRequestDTO.setEndDate(LocalDateTime.of(2025, 2, 28, 23, 59));

            when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
            when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
            when(converter.convertFromEventToEventResponse(any(Event.class)))
                    .thenReturn(new EventResponse(1L, "Updated Event Name", Collections.emptyList(),
                            LocalDateTime.of(2025, 2, 1, 0, 0),
                            LocalDateTime.of(2025, 2, 28, 23, 59)));

            EventResponse response = eventService.updateEvent(1L, createEventRequestDTO, null, Collections.emptyList());

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Updated Event Name");
            verify(eventRepository).save(any(Event.class));
        }

        @Test
        @DisplayName("이벤트 수정 실패 - 변경 사항 없음")
        void updateEventFailNoChanges() {
            when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

            assertThatThrownBy(() -> eventService.updateEvent(1L, new CreateEventRequestDTO(), null, Collections.emptyList()))
                    .isInstanceOf(NoChangeException.class)
                    .hasMessageContaining("변경된 이벤트 정보가 없습니다.");
        }

        @Test
        @DisplayName("이벤트 수정 실패 - 이벤트 없음")
        void updateEventFailNotFound() {
            when(eventRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> eventService.updateEvent(1L, createEventRequestDTO, null, Collections.emptyList()))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 이벤트가 없습니다.");
        }
    }

    @Nested
    @DisplayName("이벤트 삭제 테스트")
    class DeleteEventTests {

        @Test
        @DisplayName("이벤트 삭제 성공")
        void deleteEventSuccess() {
            when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

            String deletedEventName = eventService.deleteById(1L);

            assertThat(deletedEventName).isEqualTo("테스트 이벤트");
            verify(eventRepository).deleteById(1L);
        }

        @Test
        @DisplayName("이벤트 삭제 실패 - 이벤트 없음")
        void deleteEventFailNotFound() {
            when(eventRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> eventService.deleteById(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 이벤트가 없습니다.");
        }
    }

    @Nested
    @DisplayName("이벤트 조회 테스트")
    class RetrieveEventTests {

        @Test
        @DisplayName("ID로 이벤트 조회 성공")
        void findByIdSuccess() {
            when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
            when(converter.convertFromEventToEventResponse(any(Event.class)))
                    .thenReturn(new EventResponse(1L, "테스트 이벤트", Collections.emptyList(),
                            LocalDateTime.of(2025, 1, 1, 0, 0),
                            LocalDateTime.of(2025, 1, 31, 23, 59)));

            EventResponse response = eventService.findByIdReturnEventResponse(1L);

            assertThat(response).isNotNull();
            verify(eventRepository).findById(1L);
        }

        @Test
        @DisplayName("ID로 이벤트 조회 실패 - 이벤트 없음")
        void findByIdFailNotFound() {
            when(eventRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> eventService.findByIdReturnEventResponse(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 이벤트가 없습니다.");
        }
    }
}
