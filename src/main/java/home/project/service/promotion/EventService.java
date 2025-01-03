package home.project.service.promotion;

import home.project.dto.requestDTO.CreateEventRequestDTO;
import home.project.dto.responseDTO.EventResponse;
import home.project.dto.responseDTO.EventSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {
    EventResponse join(CreateEventRequestDTO createEventRequestDTO, MultipartFile mainImageFile, List<MultipartFile> descriptionImages);

    EventResponse updateEvent(Long eventId, CreateEventRequestDTO updateEventRequestDTO,
                              MultipartFile mainImageFile, List<MultipartFile> descriptionImages);

    Page<EventResponse> findAll(Pageable pageable);

    Page<EventSimpleResponse> findAllImages(Pageable pageable);

    Page<EventResponse> findEvents(String name, String startDate, String endDate, Integer discountRate, String content, Pageable pageable);

    EventResponse findByIdReturnEventResponse(Long eventId);

    String deleteById(Long couponId);
}
