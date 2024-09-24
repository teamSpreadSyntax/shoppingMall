package home.project.service;


import home.project.domain.Role;
import home.project.dto.RoleDTOWithMemberName;
import home.project.dto.TokenDto;
import home.project.dto.UserDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AuthService {
    TokenDto login(UserDetailsDTO userDetailsDTO);

    TokenDto refreshToken(String refreshToken);

    String logout(Long id);

    Optional<Role> addAuthority(Long id,String authority);

    String RoleMessage(Long id,String authority);

    Page<RoleDTOWithMemberName> checkAuthority(Pageable pageable);

    TokenDto verifyUser(String accessToken,String refreshToken);
}