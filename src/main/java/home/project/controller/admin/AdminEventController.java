package home.project.controller.admin;

import home.project.dto.requestDTO.CreateEventRequestDTO;
import home.project.dto.responseDTO.EventResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.promotion.EventService;
import home.project.service.util.PageUtil;
import home.project.service.util.StringBuilderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Tag(name = "관리자 이벤트", description = "관리자를 위한 이벤트 관련 API입니다")
@RequestMapping("/api/admin/event")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class AdminEventController {

    private final EventService eventService;
    private final PageUtil pageUtil;


    @Operation(summary = "이벤트 생성 메서드", description = "이벤트 생성 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/EventResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))


    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createEvent(@RequestPart("eventData") @Valid CreateEventRequestDTO createEventRequestDTO,
                                         @RequestPart(value = "mainImageFile", required = false) MultipartFile mainImageFile,
                                         @RequestPart(value = "descriptionImages", required = false) MultipartFile[] descriptionImages) {

        List<MultipartFile> imageList = descriptionImages != null ?
                Arrays.asList(descriptionImages) : new ArrayList<>();

        EventResponse eventResponse = eventService.join(createEventRequestDTO, mainImageFile, imageList);

        String successMessage = eventResponse.getName() + "(으)로 이벤트가 등록되었습니다.";

        return new CustomResponseEntity<>(eventResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "이벤트 수정 메서드", description = "이벤트 정보를 수정하는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/EventResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "event not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @PutMapping(value = "/update" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateEvent(@RequestParam("eventId") Long eventId,
                                         @RequestPart("eventData") @Valid CreateEventRequestDTO updateEventRequestDTO,
                                         @RequestPart(value = "mainImageFile", required = false) MultipartFile mainImageFile,
                                         @RequestPart(value = "descriptionImages", required = false) MultipartFile[] descriptionImages) {

        List<MultipartFile> imageList = descriptionImages != null ?
                Arrays.asList(descriptionImages) : new ArrayList<>();

        EventResponse updatedEvent = eventService.updateEvent(eventId, updateEventRequestDTO, mainImageFile, imageList);
        String successMessage = "이벤트 정보가 수정되었습니다.";
        return new CustomResponseEntity<>(updatedEvent, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "id로 이벤트 조회 메서드", description = "id로 이벤트 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/EventResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "event not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/event")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findEventById(@RequestParam("eventId") Long eventId) {
        EventResponse eventResponse = eventService.findByIdReturnEventResponse(eventId);
        String successMessage = eventId + "에 해당하는 이벤트 입니다.";
        return new CustomResponseEntity<>(eventResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체 이벤트 조회 메서드", description = "전체 이벤트 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedEventListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "event not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/events")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findAll(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "startDate", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<EventResponse> pagedEvent = eventService.findAll(pageable);

        long totalCount = pagedEvent.getTotalElements();

        int page = pagedEvent.getNumber();

        String successMessage = "전체 이벤트입니다.";

        return new CustomResponseEntity<>(pagedEvent.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "이벤트 통합 조회 메서드", description = "이벤트이름, 이벤트시작날짜, 이벤트종료날짜, 할인율 및 일반 검색어로 이벤트를 조회합니다. 모든 조건을 만족하는 이벤트를 조회합니다. 검색어가 없으면 전체 이벤트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedEventListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "event not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/search")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> searchEvent(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "discountRate", required = false) Integer discountRate,
            @RequestParam(value = "content", required = false) String content,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);

        Page<EventResponse> pagedEventResponse = eventService.findEvents(name, startDate, endDate, discountRate, content, pageable);

        String successMessage = StringBuilderUtil.buildEventSearchCriteria(name, startDate, endDate, discountRate, content, pagedEventResponse);

        long totalCount = pagedEventResponse.getTotalElements();
        int page = pagedEventResponse.getNumber();

        return new CustomResponseEntity<>(pagedEventResponse.getContent(), successMessage, HttpStatus.OK, totalCount, page);

    }

    @Operation(summary = "이벤트 삭제 메서드", description = "이벤트 삭제 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "event not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @DeleteMapping("/delete")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteEvent(@RequestParam("eventId") Long eventId) {
        String name = eventService.deleteById(eventId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", name + "(id:" + eventId + ")(이)가 삭제되었습니다.");
        return new CustomResponseEntity<>(responseMap, "이벤트 삭제 성공", HttpStatus.OK);
    }


}
