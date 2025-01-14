package home.project.controller.admin;


import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.requestDTO.VerifyUserRequestDTO;
import home.project.dto.responseDTO.MemberResponse;
import home.project.dto.responseDTO.TokenResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.member.MemberService;
import home.project.service.util.PageUtil;
import home.project.service.util.StringBuilderUtil;
import home.project.service.validation.ValidationCheck;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "관리자 회원", description = "관리자를 위한 회원관련 API입니다")
@RequestMapping(path = "/api/admin/member")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class AdminMemberController {

    private final MemberService memberService;
    private final ValidationCheck validationCheck;
    private final PageUtil pageUtil;


    @Operation(summary = "회원가입 메서드", description = "회원가입 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberJoinSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ConflictResponseSchema")))
    })
    @PostMapping("/join")
    public ResponseEntity<?> createMember(@RequestBody @Valid CreateMemberRequestDTO createMemberRequestDTO, BindingResult bindingResult) {
        CustomResponseEntity<Map<String, String>> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;

        TokenResponse TokenResponse = memberService.join(createMemberRequestDTO);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("accessToken", TokenResponse.getAccessToken());
        responseMap.put("refreshToken", TokenResponse.getRefreshToken());
        responseMap.put("role", String.valueOf(TokenResponse.getRole()));
        responseMap.put("successMessage", "회원가입이 성공적으로 완료되었습니다.");
        return new CustomResponseEntity<>(responseMap, "회원가입 성공", HttpStatus.OK);
    }

    @Operation(summary = "id로 회원 조회 메서드", description = "id로 회원 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ConflictResponseSchema")))
    })
    @GetMapping("/member")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> memberInfo() {
        MemberResponse memberResponse = memberService.memberInfo();
        Long memberId = memberResponse.getId();
        String successMessage = memberId + "(으)로 가입된 회원정보입니다";
        return new CustomResponseEntity<>(memberResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체 회원 조회 메서드", description = "전체 회원 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedMemberListResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/members")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findAllMember(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {

        pageable = pageUtil.pageable(pageable);

        Page<MemberResponse> pagedMemberResponse = memberService.findAllReturnPagedMemberResponse(pageable);


        String successMessage = "전체 회원입니다.";
        long totalCount = pagedMemberResponse.getTotalElements();
        int page = pagedMemberResponse.getNumber();
        return new CustomResponseEntity<>(pagedMemberResponse.getContent(), successMessage, HttpStatus.OK, totalCount, page);

    }

    @Operation(summary = "회원 통합 조회 메서드", description = "이름, 이메일, 전화번호 및 일반 검색어로 회원을 조회합니다. 모든 조건을 만족하는 회원을 조회합니다. 검색어가 없으면 전체 회원을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedMemberListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/search")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> searchMembers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "content", required = false) String content,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);

        Page<MemberResponse> pagedMemberResponse = memberService.findMembersOnElasticForManaging(name, email, phone, role, content, pageable);

        String successMessage = StringBuilderUtil.buildMemberSearchCriteria(name, email, phone, role, content, pagedMemberResponse);

        long totalCount = pagedMemberResponse.getTotalElements();
        int page = pagedMemberResponse.getNumber();

        return new CustomResponseEntity<>(pagedMemberResponse.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "본인확인 메서드", description = "본인확인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/VerifyResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @PostMapping("/verify")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> verifyUser(@RequestBody @Valid VerifyUserRequestDTO password, BindingResult bindingResult) {
        CustomResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) {
            return validationResponse;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        String verificationToken = memberService.verifyUser(email, password);
        Map<String, String> response = new HashMap<>();
        response.put("successMessage", "본인 확인이 완료되었습니다.");
        response.put("verificationToken", verificationToken);

        return new CustomResponseEntity<>(response, "본인 확인 성공", HttpStatus.OK);

    }

    @Operation(summary = "회원 삭제 메서드", description = "회원 삭제 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("/delete")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteMember(@RequestParam("memberId") Long memberId) {
        String email = memberService.deleteById(memberId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", email + "(id:" + memberId + ")님의 계정이 삭제되었습니다.");
        return new CustomResponseEntity<>(Optional.of(responseMap), "회원 삭제 성공", HttpStatus.OK);
    }

    @Operation(summary = "회원 탈퇴 메서드", description = "회원 탈퇴 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("/cancel")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> cancelMember(@RequestParam("memberId") Long memberId,
                                          @RequestParam("verificationToken") String verificationToken) {
        String email = memberService.cancelMember(memberId, verificationToken);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", email + "(id:" + memberId + ")님의 계정이 탈퇴되었습니다. 이용해주셔서 감사합니다.");
        return new CustomResponseEntity<>(Optional.of(responseMap), "회원 탈퇴 성공", HttpStatus.OK);
    }

    @Operation(summary = "회원 포인트 수정 메서드", description = "회원 포인트 수정 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("increase_point")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updatePoint(@RequestParam("memberId") Long memberId, @RequestParam("point") Long point) {
        MemberResponse memberResponse = memberService.updatePoint(memberId, point);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", "포인트 수정 성공. "+memberResponse.getEmail() + "님의 잔여 포인트 : "+memberResponse.getPoint());
        return new CustomResponseEntity<>(Optional.of(responseMap), "포인트 수정 성공", HttpStatus.OK);
    }

}