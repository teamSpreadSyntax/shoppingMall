package home.project.service.promotion;

import home.project.domain.product.Coupon;
import home.project.domain.promotion.Event;
import home.project.domain.promotion.EventCoupon;
import home.project.dto.requestDTO.CreateEventRequestDTO;
import home.project.dto.responseDTO.EventResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.promotion.EventRepository;
import home.project.repository.promotion.EventCouponRepository;
import home.project.service.util.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventCouponRepository eventCouponRepository;
    @Mock
    private CouponService couponService;
    @Mock
    private Converter converter;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event testEvent;
    private Coupon testCoupon;
    private EventCoupon testEventCoupon;
    private CreateEventRequestDTO createEventRequestDTO;
    private EventResponse eventResponse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        // Event 설정
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setName("TestEvent");
        testEvent.setDescription("Test Description");
        testEvent.setStartDate(now);
        testEvent.setEndDate(now.plusDays(30));
        testEvent.setImage("test-image.jpg");

        // Coupon 설정
        testCoupon = new Coupon();
        testCoupon.setId(1L);
        testCoupon.setName("TestCoupon");

        // EventCoupon 설정
        testEventCoupon = new EventCoupon();
        testEventCoupon.setEvent(testEvent);
        testEventCoupon.setCoupon(testCoupon);
        testEventCoupon.setUsed(false);

        // CreateEventRequestDTO 설정
        createEventRequestDTO = new CreateEventRequestDTO();
        createEventRequestDTO.setName("TestEvent");
        createEventRequestDTO.setDescription("Test Description");
        createEventRequestDTO.setStartDate(now);
        createEventRequestDTO.setEndDate(now.plusDays(30));
        createEventRequestDTO.setImage("test-image.jpg");
        createEventRequestDTO.setCouponId(1L);

        // EventResponse 설정 - 수정된 생성자 사용
        eventResponse = new EventResponse(
                1L,
                "TestEvent",
                "Test Description",
                now,
                now.plusDays(30),
                "test-image.jpg"
        );
    }

    @Nested
    @DisplayName("이벤트 생성 테스트")
    class CreateEventTest {

        @Test
        @DisplayName("정상적으로 이벤트를 생성한다")
        void createEventSuccess() {
            // given
            when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
            when(couponService.findById(anyLong())).thenReturn(testCoupon);
            when(converter.convertFromEventToEventResponse(any(Event.class))).thenReturn(eventResponse);

            // when
            EventResponse response = eventService.join(createEventRequestDTO);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("TestEvent");
            assertThat(response.getDescription()).isEqualTo("Test Description");
            assertThat(response.getStartDate()).isEqualTo(now);
            assertThat(response.getEndDate()).isEqualTo(now.plusDays(30));
            assertThat(response.getImage()).isEqualTo("test-image.jpg");
            verify(eventRepository).save(any(Event.class));
            verify(eventCouponRepository).save(any(EventCoupon.class));
        }
    }

    @Nested
    @DisplayName("이벤트 조회 테스트")
    class FindEventTest {

        @Test
        @DisplayName("ID로 이벤트를 조회한다")
        void findByIdSuccess() {
            // given
            when(eventRepository.findById(anyLong())).thenReturn(Optional.of(testEvent));
            when(converter.convertFromEventToEventResponse(any(Event.class))).thenReturn(eventResponse);

            // when
            EventResponse response = eventService.findByIdReturnEventResponse(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("TestEvent");
            assertThat(response.getDescription()).isEqualTo("Test Description");
            verify(eventRepository).findById(anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 이벤트 ID로 조회할 경우 실패한다")
        void findByIdFailNotFound() {
            // given
            when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> eventService.findByIdReturnEventResponse(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 이벤트가 없습니다");
        }

        @Test
        @DisplayName("모든 이벤트를 조회한다")
        void findAllSuccess() {
            // given
            Page<Event> eventPage = new PageImpl<>(List.of(testEvent));
            Page<EventResponse> eventResponsePage = new PageImpl<>(List.of(eventResponse));

            when(eventRepository.findAll(any(Pageable.class))).thenReturn(eventPage);
            when(converter.convertFromPagedEventToPagedEventResponse(any(Page.class))).thenReturn(eventResponsePage);

            // when
            Page<EventResponse> response = eventService.findAll(Pageable.unpaged());

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getName()).isEqualTo("TestEvent");
        }
    }

    @Nested
    @DisplayName("이벤트 업데이트 테스트")
    class UpdateEventTest {

        @Test
        @DisplayName("이벤트 정보를 업데이트한다")
        void updateEventSuccess() {
            // given
            CreateEventRequestDTO updateRequest = new CreateEventRequestDTO();
            updateRequest.setName("UpdatedEvent");
            updateRequest.setDescription("Updated Description");
            updateRequest.setImage("updated-image.jpg");

            // 수정된 생성자 사용
            EventResponse updatedResponse = new EventResponse(
                    1L,
                    "UpdatedEvent",
                    "Updated Description",
                    now,
                    now.plusDays(30),
                    "updated-image.jpg"
            );

            when(eventRepository.findById(anyLong())).thenReturn(Optional.of(testEvent));
            when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
            when(converter.convertFromEventToEventResponse(any(Event.class))).thenReturn(updatedResponse);

            // when
            EventResponse response = eventService.updateEvent(1L, updateRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("UpdatedEvent");
            assertThat(response.getDescription()).isEqualTo("Updated Description");
            assertThat(response.getImage()).isEqualTo("updated-image.jpg");
            verify(eventRepository).save(any(Event.class));
        }

        @Test
        @DisplayName("변경사항이 없는 경우 예외가 발생한다")
        void updateEventFailNoChange() {
            // given
            CreateEventRequestDTO noChangeRequest = new CreateEventRequestDTO();
            noChangeRequest.setName("TestEvent");
            noChangeRequest.setDescription("Test Description");
            noChangeRequest.setImage("test-image.jpg");

            when(eventRepository.findById(anyLong())).thenReturn(Optional.of(testEvent));

            // when & then
            assertThatThrownBy(() -> eventService.updateEvent(1L, noChangeRequest))
                    .isInstanceOf(NoChangeException.class)
                    .hasMessageContaining("변경된 이벤트 정보가 없습니다");
        }
    }

    @Nested
    @DisplayName("이벤트 삭제 테스트")
    class DeleteEventTest {

        @Test
        @DisplayName("이벤트를 삭제한다")
        void deleteEventSuccess() {
            // given
            when(eventRepository.findById(anyLong())).thenReturn(Optional.of(testEvent));

            // when
            String deletedEventName = eventService.deleteById(1L);

            // then
            assertThat(deletedEventName).isEqualTo("TestEvent");
            verify(eventRepository).deleteById(anyLong());
        }
    }
}