package home.project.service.member;


import home.project.domain.member.RoleType;
import home.project.dto.responseDTO.RoleResponse;
import home.project.dto.responseDTO.TokenResponse;
import home.project.dto.requestDTO.LoginRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthService {
    TokenResponse login(LoginRequestDTO loginRequestDTO);

    TokenResponse refreshToken(String refreshToken);

    void addAuthority(Long id, RoleType authority);

    String roleMessage(Long id, RoleType authority);

    Page<RoleResponse> checkAuthority(Pageable pageable);

    TokenResponse verifyUser(String accessToken, String refreshToken);
}