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
import home.project.service.util.FileService;
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
import org.springframework.web.multipart.MultipartFile;

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
    @Mock
    private FileService fileService;

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
        testEvent.setDescription(List.of("Test Description"));
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
        createEventRequestDTO.setStartDate(now);
        createEventRequestDTO.setEndDate(now.plusDays(30));
        createEventRequestDTO.setCouponId(1L);

        // EventResponse 설정
        eventResponse = new EventResponse(
                1L,
                "TestEvent",
                List.of("Test Description"),
                now,
                now.plusDays(30)
        );
    }

    @Nested
    @DisplayName("이벤트 생성 테스트")
    class CreateEventTest {

        @Test
        @DisplayName("정상적으로 이벤트를 생성한다")
        void createEventSuccess() {
            // given
            MultipartFile mainImageFile = mock(MultipartFile.class);
            List<MultipartFile> descriptionImages = List.of(mock(MultipartFile.class));
            when(mainImageFile.isEmpty()).thenReturn(false);
            when(fileService.saveFile(any(), anyString(), anyString())).thenReturn("test-main-image.jpg");
            when(descriptionImages.get(0).isEmpty()).thenReturn(false);
            when(fileService.saveFile(any(), anyString(), anyString())).thenReturn("test-description-image.jpg");

            when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
            when(couponService.findById(anyLong())).thenReturn(testCoupon);
            when(converter.convertFromEventToEventResponse(any(Event.class))).thenReturn(eventResponse);

            // when
            EventResponse response = eventService.join(createEventRequestDTO, mainImageFile, descriptionImages);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("TestEvent");
            assertThat(response.getDescription()).contains("Test Description");
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
            verify(eventRepository).findById(anyLong());
        }
    }

    @Test
    @DisplayName("이벤트 정보를 업데이트한다")
    void updateEventSuccess() {
        // given
        CreateEventRequestDTO updateRequest = new CreateEventRequestDTO();
        updateRequest.setName("UpdatedEvent");

        MultipartFile mainImageFile = mock(MultipartFile.class);
        List<MultipartFile> descriptionImages = List.of(mock(MultipartFile.class));

        // 기존 Event를 수정된 상태로 반환하도록 설정
        testEvent.setName("UpdatedEvent");

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        when(converter.convertFromEventToEventResponse(any(Event.class)))
                .thenReturn(new EventResponse(
                        testEvent.getId(),
                        testEvent.getName(),
                        testEvent.getDescription(),
                        testEvent.getStartDate(),
                        testEvent.getEndDate()
                ));

        // when
        EventResponse response = eventService.updateEvent(1L, updateRequest, mainImageFile, descriptionImages);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("UpdatedEvent");
        verify(eventRepository).save(any(Event.class));
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
