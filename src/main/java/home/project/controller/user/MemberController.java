package home.project.controller.user;


import home.project.dto.requestDTO.*;
import home.project.dto.responseDTO.MemberResponse;
import home.project.dto.responseDTO.MemberResponseForUser;
import home.project.dto.responseDTO.TokenResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.member.MemberService;
import home.project.service.util.PageUtil;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "회원", description = "회원관련 API입니다")
@RequestMapping(path = "/api/member")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final ValidationCheck validationCheck;
    private final PageUtil pageUtil;


    @Operation(summary = "회원가입 메서드", description = "회원가입 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member successfully registered.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberJoinSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "409", description = "Conflict: Email already exists.",
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
            @ApiResponse(responseCode = "200", description = "Member information retrieved successfully.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Not Found: Member does not exist.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @GetMapping("/member")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> memberInfo() {
        MemberResponse memberResponse = memberService.memberInfo();
        Long memberId = memberResponse.getId();
        String successMessage = memberId + "(으)로 가입된 회원정보입니다";
        return new CustomResponseEntity<>(memberResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "본인확인 메서드", description = "본인확인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification successful.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid credentials.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema")))
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

    @Operation(summary = "회원 정보 업데이트(수정) 메서드", description = "회원 정보 업데이트(수정) 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member information updated successfully.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberResponseForUserSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Not Found: Member does not exist.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @PutMapping("/update")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateMember(@RequestBody @Valid UpdateMemberRequestDTO updateMemberRequestDTO,
                                          BindingResult bindingResult,
                                          @RequestParam("verificationToken") String verificationToken) {
        CustomResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) {
            return validationResponse;
        }

        MemberResponseForUser MemberResponseForUser = memberService.update(updateMemberRequestDTO, verificationToken);

        String successMessage = "회원 정보가 수정되었습니다.";

        return new CustomResponseEntity<>(MemberResponseForUser, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "회원 탈퇴 메서드", description = "회원 탈퇴 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member account deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Not Found: Member does not exist.",
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

    @Operation(summary = "이메일 찾기 메서드", description = "이름과 전화번호를 기반으로 사용자의 이메일을 찾습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email found successfully.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Not Found: Member does not exist.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @PostMapping("/find-email")
    public ResponseEntity<?> findEmail(@RequestBody @Valid FindEmailRequestDTO findEmailRequestDTO, BindingResult bindingResult) {
        CustomResponseEntity<Map<String, String>> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;

        String email = memberService.findEmail(findEmailRequestDTO.getName(), findEmailRequestDTO.getPhone());

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("email", email);
        responseMap.put("successMessage", "이메일 찾기에 성공했습니다.");
        return new CustomResponseEntity<>(responseMap, "이메일 찾기 성공", HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 재설정 요청", description = "비밀번호 재설정을 위해 이메일로 링크를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email sent successfully.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Not Found: Member does not exist.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/reset-password-request")

    public ResponseEntity<?> resetPasswordRequest(@RequestBody @Valid PasswordResetRequestDTO requestDTO, BindingResult bindingResult) {
        CustomResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;

        memberService.sendPasswordResetEmail(requestDTO.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("successMessage", "비밀번호 재설정 링크가 이메일로 전송되었습니다.");
        return new CustomResponseEntity<>(response, "비밀번호 재설정 요청 성공", HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 재설정", description = "토큰을 검증하고 비밀번호를 재설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema")))
    })
    @PostMapping("/reset_password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @RequestBody @Valid PasswordResetDTO passwordResetDTO,
                                           BindingResult bindingResult) {
        CustomResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;

        memberService.resetPassword(token, passwordResetDTO.getNewPassword());

        Map<String, String> response = new HashMap<>();
        response.put("successMessage", "비밀번호가 성공적으로 재설정되었습니다.");
        return new CustomResponseEntity<>(response, "비밀번호 재설정 성공", HttpStatus.OK);
    }
}