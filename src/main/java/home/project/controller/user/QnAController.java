package home.project.controller.user;

import home.project.dto.requestDTO.CreateQnARequestDTO;
import home.project.dto.responseDTO.QnADetailResponse;
import home.project.dto.responseDTO.QnAResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.common.QnAService;
import home.project.service.util.PageUtil;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "QnA", description = "QnA 관련 API입니다.")
@RequestMapping("/api/qna")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class QnAController {

    private final QnAService qnAService;
    private final PageUtil pageUtil;

    @Operation(summary = "QnA 작성", description = "새로운 QnA를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/QnADetailResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "권한 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/join")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createQnA(@RequestBody CreateQnARequestDTO createQnARequestDTO) {
        QnADetailResponse qnADetailResponse = qnAService.join(createQnARequestDTO);
        String successMessage = "QnA가 성공적으로 작성되었습니다.";
        return new CustomResponseEntity<>(qnADetailResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "ID로 QnA 조회", description = "QnA ID를 통해 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/QnADetailResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/qna_detail")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findQnAById(@RequestParam("qnAId") Long qnAId) {
        QnADetailResponse qnADetailResponse = qnAService.findByIdReturnQnADetailResponse(qnAId);
        String successMessage = qnAId + "번 QnA의 상세 정보를 조회했습니다.";
        return new CustomResponseEntity<>(qnADetailResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체 QnA 조회", description = "모든 QnA를 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/QnAResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/qnas")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findAll(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "qnAId", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<QnAResponse> pagedQnA = qnAService.findAll(pageable);

        long totalCount = pagedQnA.getTotalElements();
        int page = pagedQnA.getNumber();
        String successMessage = "모든 QnA를 조회했습니다.";

        return new CustomResponseEntity<>(pagedQnA.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "상품 관련 QnA 조회", description = "특정 상품과 관련된 QnA를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/QnAResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/product_qna")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findProductQnA(@RequestParam(value = "productId") Long productId,
                                            @PageableDefault(page = 1, size = 5)
                                            @SortDefault.SortDefaults(
                                                    {@SortDefault(sort = "qnAId", direction = Sort.Direction.ASC)})
                                            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<QnAResponse> pagedQnA = qnAService.findProductQnA(productId, pageable);

        long totalCount = pagedQnA.getTotalElements();
        int page = pagedQnA.getNumber();
        String successMessage = "상품에 대한 QnA를 조회했습니다.";

        return new CustomResponseEntity<>(pagedQnA.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "내 QnA 조회", description = "로그인한 사용자의 QnA를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/QnAResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/my_qna")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findMyQnA(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults(
                    {@SortDefault(sort = "qnAId", direction = Sort.Direction.ASC)})
            @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        Page<QnAResponse> pagedQnA = qnAService.findAllMyQnA(pageable);

        long totalCount = pagedQnA.getTotalElements();
        int page = pagedQnA.getNumber();
        String successMessage = "내 QnA를 조회했습니다.";

        return new CustomResponseEntity<>(pagedQnA.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "QnA 삭제", description = "특정 QnA를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("/delete")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteQnA(@RequestParam("qnAId") Long qnAId) {
        qnAService.deleteById(qnAId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", qnAId + "번 QnA가 삭제되었습니다.");
        return new CustomResponseEntity<>(responseMap, "QnA 삭제 성공", HttpStatus.OK);
    }
}
