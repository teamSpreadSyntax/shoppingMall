package home.project.service;


import home.project.domain.Role;
import home.project.dto.TokenDto;
import home.project.dto.UserDetailsDTO;

import java.util.Optional;

public interface AuthService {
    TokenDto login(UserDetailsDTO userDetailsDTO);

    TokenDto refreshToken(String refreshToken);

    String logout(Long id);

    Optional<Role> addAuthority(Long id,String authority);

    String RoleMessage(Long id,String authority);
}