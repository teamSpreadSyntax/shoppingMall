package home.project.service;

import home.project.dto.requestDTO.AssignCouponToMemberRequestDTO;
import home.project.dto.requestDTO.AssignCouponToProductRequestDTO;
import home.project.dto.requestDTO.CreateCouponRequestDTO;
import home.project.dto.requestDTO.CreateEventRequestDTO;
import home.project.dto.responseDTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface EventService {
    EventResponse join(CreateEventRequestDTO createEventRequestDTO);

    EventResponse updateEvent(Long eventId, CreateEventRequestDTO updateEventRequestDTO);

    Page<EventResponse> findAll(Pageable pageable);

    Page<EventResponse> findEvents(String name, String startDate, String endDate, Integer discountRate, String content, Pageable pageable);

    EventResponse findByIdReturnEventResponse(Long eventId);

    String deleteById(Long couponId);
}
