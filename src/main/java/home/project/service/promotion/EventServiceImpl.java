package home.project.service.promotion;

import home.project.domain.product.Coupon;
import home.project.domain.promotion.Event;
import home.project.domain.promotion.EventCoupon;
import home.project.dto.requestDTO.CreateEventRequestDTO;
import home.project.dto.responseDTO.EventResponse;
import home.project.dto.responseDTO.EventSimpleResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.promotion.*;
import home.project.service.util.Converter;
import home.project.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;
    private final EventCouponRepository eventCouponRepository;
    private final CouponService couponService;
    private final Converter converter;
    private final FileService fileService;


    @Override
    @Transactional
    public EventResponse join(CreateEventRequestDTO createEventRequestDTO, MultipartFile mainImageFile, List<MultipartFile> descriptionImages){
        Event event = new Event();
        event.setName(createEventRequestDTO.getName());
        event.setStartDate(createEventRequestDTO.getStartDate());
        event.setEndDate(createEventRequestDTO.getEndDate());

        String mainImageUrl = null;
        if (mainImageFile != null && !mainImageFile.isEmpty()) {
            mainImageUrl = fileService.saveFile(mainImageFile, "event/main", "center");
        }
        event.setImage(mainImageUrl);

        List<String> descriptionImageUrls = descriptionImages.stream()
                .map(file -> fileService.saveFile(file, "event/description", "center"))
                .collect(Collectors.toList());
        event.setDescription(descriptionImageUrls); // 기타 이미지 URL 설정

        eventRepository.save(event);

        EventCoupon eventCoupon = new EventCoupon();
        Coupon coupon = couponService.findById(createEventRequestDTO.getCouponId());
        eventCoupon.setCoupon(coupon);
        eventCoupon.setEvent(event);
        eventCoupon.setUsed(false);

        eventCouponRepository.save(eventCoupon);

        return converter.convertFromEventToEventResponse(event);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long eventId, CreateEventRequestDTO updateEventRequestDTO,
                                     MultipartFile mainImageFile, List<MultipartFile> descriptionImages) {

        Event existingEvent = findById(eventId);

        boolean isModified = false;

        if (updateEventRequestDTO.getName() != null && !updateEventRequestDTO.getName().equals(existingEvent.getName())) {
            existingEvent.setName(updateEventRequestDTO.getName());
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


        if (mainImageFile != null && !mainImageFile.isEmpty()) {
            String mainImageUrl = fileService.saveFile(mainImageFile, "event/main", "center");
            existingEvent.setImage(mainImageUrl);
            isModified = true;
        }

        if (descriptionImages != null && !descriptionImages.isEmpty()) {
            List<String> descriptionImageUrls = descriptionImages.stream()
                    .map(file -> fileService.saveFile(file, "event/description", "center"))
                    .collect(Collectors.toList());
            existingEvent.setDescription(descriptionImageUrls);
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
    public Page<EventSimpleResponse> findAllImages(Pageable pageable) {
        Page<Event> pagedEvent= eventRepository.findAll(pageable);
        return converter.convertFromPagedEventToPagedEventSimpleResponse(pagedEvent);
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
