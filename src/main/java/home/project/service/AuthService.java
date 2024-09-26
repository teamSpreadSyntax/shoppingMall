package home.project.service;


import home.project.domain.Role;
import home.project.domain.RoleType;
import home.project.dto.responseDTO.RoleResponseDTO;
import home.project.dto.responseDTO.TokenResponseDTO;
import home.project.dto.requestDTO.LoginRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AuthService {
    TokenResponseDTO login(LoginRequestDTO loginRequestDTO);

    TokenResponseDTO refreshToken(String refreshToken);

    String logout(Long id);

    Optional<Role> addAuthority(Long id, RoleType authority);

    String roleMessage(Long id, RoleType authority);

    Page<RoleResponseDTO> checkAuthority(Pageable pageable);

    TokenResponseDTO verifyUser(String accessToken, String refreshToken);
}