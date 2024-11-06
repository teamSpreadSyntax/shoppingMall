package home.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.*;
import home.project.dto.requestDTO.CreateEventRequestDTO;
import home.project.dto.responseDTO.EventResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;
    private final EventCouponRepository eventCouponRepository;
    private final CouponService couponService;
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final ProductCouponRepository productCouponRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Converter converter;


    @Override
    @Transactional
    public EventResponse join(CreateEventRequestDTO createEventRequestDTO){
        Event event = new Event();
        event.setName(createEventRequestDTO.getName());
        event.setDescription(createEventRequestDTO.getDescription());
        event.setStartDate(createEventRequestDTO.getStartDate());
        event.setEndDate(createEventRequestDTO.getEndDate());
        event.setImage(createEventRequestDTO.getImage());

        eventRepository.save(event);

        EventCoupon eventCoupon = new EventCoupon();
        Coupon coupon = couponService.findById(createEventRequestDTO.getCouponId());
        eventCoupon.setCoupon(coupon);
        eventCoupon.setEvent(event);
        eventCoupon.setUsed(false);

        eventCouponRepository.save(eventCoupon);

//        sendCouponEvent(new CouponEventDTO("coupon_created", coupon.getId()));

        return converter.convertFromEventToEventResponse(event);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long eventId, CreateEventRequestDTO updateEventRequestDTO) {

        Event existingEvent = findById(eventId);

        boolean isModified = false;

        if (updateEventRequestDTO.getName() != null && !updateEventRequestDTO.getName().equals(existingEvent.getName())) {
            existingEvent.setName(updateEventRequestDTO.getName());
            isModified = true;
        }

        if (updateEventRequestDTO.getDescription() != null && !updateEventRequestDTO.getDescription().equals(existingEvent.getDescription())) {
            existingEvent.setDescription(updateEventRequestDTO.getDescription());
            isModified = true;
        }

        if (updateEventRequestDTO.getStartDate() != null && !updateEventRequestDTO.getStartDate().equals(existingEvent.getStartDate())) {
            existingEvent.setStartDate(updateEventRequestDTO.getStartDate());
            isModified = true;
        }

        if (updateEventRequestDTO.getEndDate() != null && !updateEventRequestDTO.getEndDate().equals(existingEvent.getEndDate())) {
            existingEvent.setEndDate(updateEventRequestDTO.getEndDate());
            isModified = true;
        }

        if (updateEventRequestDTO.getImage() != null && !updateEventRequestDTO.getImage().equals(existingEvent.getImage())) {
            existingEvent.setImage(updateEventRequestDTO.getImage());
            isModified = true;
        }

        if (!isModified) {
            throw new NoChangeException("변경된 이벤트 정보가 없습니다.");
        }

        eventRepository.save(existingEvent);
        return converter.convertFromEventToEventResponse(existingEvent);
    }
    @Override
    public Page<EventResponse> findAll(Pageable pageable) {
        Page<Event> pagedEvent= eventRepository.findAll(pageable);
        return converter.convertFromPagedEventToPagedEventResponse(pagedEvent);
    }

    @Override
    public EventResponse findByIdReturnEventResponse(Long eventId) {
        return converter.convertFromEventToEventResponse(findById(eventId));
    }


//    private void sendCouponEvent(CouponEventDTO event) {
//        try {
//            String message = objectMapper.writeValueAsString(event);
//            kafkaTemplate.send("coupon-events", message);
//        } catch (JsonProcessingException e) {
//            // 에러 처리
//            e.printStackTrace();
//        }
//    }???

    private Event findById(Long eventId){
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IdNotFoundException(eventId + "(으)로 등록된 이벤트가 없습니다."));
    }

    @Override
    public Page<EventResponse> findEvents(String name, String startDate, String endDate, Integer discountRate, String content, Pageable pageable) {

        Page<Event> pagedEvent = eventRepository.findEvents(name, startDate, endDate, discountRate, content, pageable);

        return converter.convertFromPagedEventToPagedEventResponse(pagedEvent);
    }

    @Override
    @Transactional
    public String deleteById(Long eventId) {
        String name = findById(eventId).getName();
        eventRepository.deleteById(eventId);
        return name;
    }


}
