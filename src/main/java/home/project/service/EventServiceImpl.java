package home.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.*;
import home.project.dto.CouponEventDTO;
import home.project.dto.requestDTO.AssignCouponToMemberRequestDTO;
import home.project.dto.requestDTO.AssignCouponToProductRequestDTO;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.requestDTO.CreateEventRequestDTO;
import home.project.dto.responseDTO.CouponResponse;
import home.project.dto.responseDTO.EventResponse;
import home.project.dto.responseDTO.MemberCouponResponse;
import home.project.dto.responseDTO.ProductCouponResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.*;
import home.project.util.StringBuilderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;
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
        event.setDiscountRate(createEventRequestDTO.getDiscountRate());
        event.setDescription(createEventRequestDTO.getDescription());
        event.setStartDate(createEventRequestDTO.getStartDate());
        event.setEndDate(createEventRequestDTO.getEndDate());
        event.setImage(createEventRequestDTO.getImage());

        eventRepository.save(event);

//        sendCouponEvent(new CouponEventDTO("coupon_created", coupon.getId()));

        return converter.convertFromEventToEventResponse(event);
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
