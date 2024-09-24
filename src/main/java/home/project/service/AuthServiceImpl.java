package home.project.service;

import home.project.domain.Member;
import home.project.domain.Role;
import home.project.dto.RoleDTOWithMemberName;
import home.project.dto.TokenDto;
import home.project.dto.UserDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final RoleService roleService;

    @Override
    public TokenDto login(UserDetailsDTO userDetailsDTO) {
        UserDetails member = userDetailsService.loadUserByUsername(userDetailsDTO.getEmail());
        if (!passwordEncoder.matches(userDetailsDTO.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호를 확인해주세요.");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetailsDTO.getEmail(), userDetailsDTO.getPassword()));
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        Long id = memberService.findByEmail(userDetailsDTO.getEmail()).get().getId();
        String role = roleService.findById(id).get().getRole();
        tokenDto.setRole(role);
        return tokenDto;
    }

    @Override
    public TokenDto refreshToken(String refreshToken) {
        TokenDto newTokenDto = jwtTokenProvider.refreshAccessToken(refreshToken);

        String email = jwtTokenProvider.getEmailFromToken(newTokenDto.getAccessToken());
        Optional<Member> member = memberService.findByEmail(email);

        Long id = member.get().getId();
        Optional<Role> roleOptional = roleService.findById(id);

        String role = roleOptional.get().getRole();
        newTokenDto.setRole(role);
        return newTokenDto;
    }


    @Override
    public String logout(Long memberId) {
        Optional<Member> member = memberService.findById(memberId);
        Optional<Role> role = roleService.findById(memberId);

        roleService.update(role.get());
        String email = member.get().getEmail();
        return email;
    }

    @Override
    public Optional<Role> addAuthority(Long id, String authority) {
        Role role = roleService.findById(id).get();
        role.setId(id);
        role.setRole(authority);
        roleService.update(role);
        return Optional.of(role);
    }

    public String RoleMessage(Long id, String authority) {
        String successMessage = "";
        Role role = roleService.findById(id).get();
        String name = memberService.findById(id).get().getName();
        if (authority.equals("admin")) {
            role.setRole("admin");
            successMessage = name + "(id : " + id + ")" + "님에게 중간 관리자 권한을 부여했습니다.";
        } else if (authority.equals("center")) {
            role.setRole("center");
            successMessage = name + "(id : " + id + ")" + "님에게 중앙 관리자 권한을 부여했습니다.";
        } else if (authority.equals("user")) {
            role.setRole("user");
            successMessage = name + "(id : " + id + ")" + "님에게 일반 사용자 권한을 부여했습니다.";
        }
        return successMessage;
    }

    @Override
    public Page<RoleDTOWithMemberName> checkAuthority(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        Page<Member> memberPage = memberService.findAll(pageable);
        Page<RoleDTOWithMemberName> rolesWithMemberNamesPage = memberPage.map(member -> {
            String role = roleService.findById(member.getId()).get().getRole();
            return new RoleDTOWithMemberName(member.getId(), role, member.getName());});
        return rolesWithMemberNamesPage;
    }

    @Override
    public TokenDto verifyUser(String accessToken, String refreshToken) {
        jwtTokenProvider.validateTokenResult(accessToken,refreshToken);
        String email = jwtTokenProvider.getEmailFromToken(accessToken);
        Optional<Member> member = memberService.findByEmail(email);

        Long id = member.get().getId();
        Optional<Role> roleOptional = roleService.findById(id);

        String role = roleOptional.get().getRole();
        TokenDto newTokenDto = new TokenDto();
        newTokenDto.setRole(role);
        newTokenDto.setAccessToken(accessToken);
        newTokenDto.setRefreshToken(refreshToken);
        newTokenDto.setGrantType("Bearer");
        return newTokenDto;
    }
}