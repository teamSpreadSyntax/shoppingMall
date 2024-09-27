package home.project.service;


import home.project.domain.Role;
import home.project.domain.RoleType;
import home.project.dto.responseDTO.RoleResponse;
import home.project.dto.responseDTO.TokenResponse;
import home.project.dto.requestDTO.LoginRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AuthService {
    TokenResponse login(LoginRequestDTO loginRequestDTO);

    TokenResponse refreshToken(String refreshToken);

    String logout(Long id);

    Role addAuthority(Long id, RoleType authority);

    String roleMessage(Long id, RoleType authority);

    Page<RoleResponse> checkAuthority(Pageable pageable);

    TokenResponse verifyUser(String accessToken, String refreshToken);
}