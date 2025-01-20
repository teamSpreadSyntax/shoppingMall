package home.project.controller.user;

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
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이벤트", description = "이벤트 관련 API입니다.")
@RequestMapping("/api/event")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class EventController {

    private final EventService eventService;
    private final PageUtil pageUtil;

    @Operation(summary = "ID로 이벤트 조회", description = "ID를 통해 특정 이벤트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/EventResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/event")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findEventById(@RequestParam("eventId") Long eventId) {
        EventResponse eventResponse = eventService.findByIdReturnEventResponse(eventId);
        String successMessage = eventId + "에 해당하는 이벤트입니다.";
        return new CustomResponseEntity<>(eventResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체 이벤트 조회", description = "전체 이벤트를 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/EventResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
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
        String successMessage = "전체 이벤트 조회 결과입니다.";

        return new CustomResponseEntity<>(pagedEvent.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "이벤트 검색", description = "이벤트 이름, 시작 날짜, 종료 날짜, 할인율 및 검색어를 사용하여 이벤트를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/EventResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
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
}
