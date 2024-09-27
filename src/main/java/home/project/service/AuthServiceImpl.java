package home.project.service;

import home.project.domain.Member;
import home.project.domain.Role;
import home.project.domain.RoleType;
import home.project.dto.responseDTO.RoleResponse;
import home.project.dto.responseDTO.TokenResponse;
import home.project.dto.requestDTO.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static home.project.domain.RoleType.*;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final RoleService roleService;

    @Override
    public TokenResponse login(LoginRequestDTO loginRequestDTO) {
        UserDetails member = userDetailsService.loadUserByUsername(loginRequestDTO.getEmail());
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호를 확인해주세요.");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));
        TokenResponse TokenResponse = jwtTokenProvider.generateToken(authentication);

        Long id = memberService.findByEmail(loginRequestDTO.getEmail()).getId();
        RoleType role = roleService.findById(id).getRole();
        TokenResponse.setRole(role);
        return TokenResponse;
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        TokenResponse newTokenDto = jwtTokenProvider.refreshAccessToken(refreshToken);

        String email = jwtTokenProvider.getEmailFromToken(newTokenDto.getAccessToken());
        Member member = memberService.findByEmail(email);

        Long id = member.getId();
        Role role = roleService.findById(id);

        RoleType roleType = role.getRole();
        newTokenDto.setRole(roleType);
        return newTokenDto;
    }


    @Override
    @Transactional
    public String logout(Long memberId) {
        Member member = memberService.findById(memberId);
        Role role = roleService.findById(memberId);

        roleService.update(role);

        return member.getEmail();
    }

    @Override
    public Role addAuthority(Long id, RoleType authority) {
        Role role = roleService.findById(id);
        role.setId(id);
        role.setRole(authority);
        return roleService.update(role);
    }

    public String roleMessage(Long id, RoleType authority) {
        Role role = roleService.findById(id);
        String name = memberService.findById(id).getName();
        String successMessage = "";
        if (authority.equals(admin)) {
            role.setRole(admin);
            successMessage = name + "(id : " + id + ")" + "님에게 중간 관리자 권한을 부여했습니다.";
        } else if (authority.equals(center)) {
            role.setRole(center);
            successMessage = name + "(id : " + id + ")" + "님에게 중앙 관리자 권한을 부여했습니다.";
        } else if (authority.equals(user)) {
            role.setRole(user);
            successMessage = name + "(id : " + id + ")" + "님에게 일반 사용자 권한을 부여했습니다.";
        }
        return successMessage;
    }

    @Override
    public Page<RoleResponse> checkAuthority(Pageable pageable) {
        Page<Member> pagedMember = memberService.findAll(pageable);
        Page<RoleResponse> pagedRoleResponse = pagedMember.map(member -> {
            RoleType role = roleService.findById(member.getId()).getRole();
            return new RoleResponse(member.getId(), role, member.getName());
        });
        return pagedRoleResponse;
    }

    @Override
    public TokenResponse verifyUser(String accessToken, String refreshToken) {
        jwtTokenProvider.validateTokenResult(accessToken, refreshToken);
        String email = jwtTokenProvider.getEmailFromToken(accessToken);
        Member member = memberService.findByEmail(email);

        Long id = member.getId();
        Role role = roleService.findById(id);

        RoleType roleType = role.getRole();
        TokenResponse newTokenDto = new TokenResponse();
        newTokenDto.setRole(roleType);
        newTokenDto.setAccessToken(accessToken);
        newTokenDto.setRefreshToken(refreshToken);
        newTokenDto.setGrantType("Bearer");
        return newTokenDto;
    }
}