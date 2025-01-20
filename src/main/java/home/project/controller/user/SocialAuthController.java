package home.project.controller.user;

import home.project.dto.requestDTO.CreateSocialMemberRequestDTO;
import home.project.dto.requestDTO.SocialLoginRequestDTO;
import home.project.dto.responseDTO.TokenResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.member.AuthService;
import home.project.service.member.MemberService;
import home.project.service.util.PageUtil;
import home.project.service.validation.ValidationCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "소셜 로그인", description = "소셜 로그인 API입니다.")
@RequestMapping(path = "/api/social")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class SocialAuthController {

    private final ValidationCheck validationCheck;
    private final AuthService authService;
    private final PageUtil pageUtil;
    private final MemberService memberService;

    @Operation(summary = "소셜 로그인 메서드", description = "소셜 로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Social login successful",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/TokenResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid login credentials",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/LoginValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid SocialLoginRequestDTO socialLoginRequestDTO, BindingResult bindingResult) {
        CustomResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;

        String email = socialLoginRequestDTO.getEmail();

        memberService.findByEmail(email);

        TokenResponse TokenResponse = authService.socialLogin(email);

        String successMessage = email + "(으)로 소셜 로그인에 성공했습니다.";

        return new CustomResponseEntity<>(TokenResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "회원가입 메서드", description = "회원가입 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member registered successfully",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberJoinSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "409", description = "Email already registered",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ConflictResponseSchema")))
    })
    @PostMapping("/join")
    public ResponseEntity<?> createMember(@RequestBody @Valid CreateSocialMemberRequestDTO createSocialMemberRequestDTO, BindingResult bindingResult) {
        CustomResponseEntity<Map<String, String>> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;

        TokenResponse TokenResponse = memberService.socialJoin(createSocialMemberRequestDTO);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("accessToken", TokenResponse.getAccessToken());
        responseMap.put("refreshToken", TokenResponse.getRefreshToken());
        responseMap.put("role", String.valueOf(TokenResponse.getRole()));
        responseMap.put("successMessage", "회원가입이 성공적으로 완료되었습니다.");
        return new CustomResponseEntity<>(responseMap, "회원가입 성공", HttpStatus.OK);
    }

}
