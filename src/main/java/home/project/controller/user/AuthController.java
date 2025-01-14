package home.project.controller.user;

import home.project.dto.requestDTO.LoginRequestDTO;
import home.project.dto.responseDTO.TokenResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.member.AuthService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "로그인, 로그아웃, 권한", description = "로그인, 로그아웃, 권한 관련 API입니다.")
@RequestMapping(path = "/api/auth")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServer")))
})
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final ValidationCheck validationCheck;
    private final AuthService authService;

    @Operation(summary = "로그인 메서드", description = "사용자가 이메일과 비밀번호를 통해 로그인합니다.")
            @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Successful operation",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/TokenResponseSchema"))),
                    @ApiResponse(responseCode = "400", description = "Bad Request: The request data is invalid.",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid credentials.",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
                    @ApiResponse(responseCode = "404", description = "Not Found: User not found.",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO, BindingResult bindingResult) {
        CustomResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;

        TokenResponse tokenResponse = authService.login(loginRequestDTO);

        return new CustomResponseEntity<>(tokenResponse, "로그인 성공", HttpStatus.OK);
    }

    @Operation(summary = "토큰 갱신 메서드", description = "만료된 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/TokenResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid refresh token.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden: Refresh token has expired.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Not Found: User not found.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam(value = "refreshToken") String refreshToken) {
        TokenResponse newTokenDto = authService.refreshToken(refreshToken);
        return new CustomResponseEntity<>(newTokenDto, "토큰이 성공적으로 갱신되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "토큰 유효성 확인 메서드", description = "액세스 토큰과 리프레시 토큰의 유효성을 검증합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/TokenResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request: The request data is invalid.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid access token or refresh token.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden: Access token has expired.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema")))
    })
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(
            @RequestParam(value = "accessToken") String accessToken,
            @RequestParam(value = "refreshToken") String refreshToken) {
        TokenResponse newTokenDto = authService.verifyUser(accessToken, refreshToken);
        return new CustomResponseEntity<>(newTokenDto, "토큰이 검증되었습니다.", HttpStatus.OK);
    }
}
