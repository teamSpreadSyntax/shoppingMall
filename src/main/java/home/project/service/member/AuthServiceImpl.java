package home.project.service.member;

import home.project.domain.member.Member;
import home.project.domain.member.RoleType;
import home.project.dto.requestDTO.LoginRequestDTO;
import home.project.dto.responseDTO.RoleResponse;
import home.project.dto.responseDTO.TokenResponse;
import home.project.repository.member.MemberRepository;
import home.project.service.security.JwtTokenProvider;
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

import static home.project.domain.member.RoleType.*;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Override
    public TokenResponse login(LoginRequestDTO loginRequestDTO) {
        UserDetails member = userDetailsService.loadUserByUsername(loginRequestDTO.getEmail());
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호를 확인해주세요.");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));
        TokenResponse TokenResponse = jwtTokenProvider.generateToken(authentication);

        Long id = memberService.findByEmail(loginRequestDTO.getEmail()).getId();
        RoleType role = memberService.findById(id).getRole();
        TokenResponse.setRole(role);
        return TokenResponse;
    }

    @Override
    public TokenResponse socialLogin(String email) {
        UserDetails member = userDetailsService.loadUserByUsername(email);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, "null"));
        TokenResponse TokenResponse = jwtTokenProvider.generateToken(authentication);

        Long id = memberService.findByEmail(email).getId();
        RoleType role = memberService.findById(id).getRole();
        TokenResponse.setRole(role);
        return TokenResponse;
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        TokenResponse newTokenDto = jwtTokenProvider.refreshAccessToken(refreshToken);

        String email = jwtTokenProvider.getEmailFromToken(newTokenDto.getAccessToken());
        Member member = memberService.findByEmail(email);

        Long id = member.getId();
        RoleType role = memberService.findById(id).getRole();

        newTokenDto.setRole(role);
        return newTokenDto;
    }


    @Override
    @Transactional
    public void addAuthority(Long id, RoleType authority) {
        Member member = memberService.findById(id);
        member.setRole(authority);

        memberRepository.save(member);
    }

    @Override
    public String roleMessage(Long id, RoleType authority) {
        String name = memberService.findById(id).getName();
        String successMessage = "";
        if (authority.equals(admin)) {
            successMessage = name + "(id : " + id + ")" + "님에게 중간 관리자 권한을 부여했습니다.";
        } else if (authority.equals(center)) {
            successMessage = name + "(id : " + id + ")" + "님에게 중앙 관리자 권한을 부여했습니다.";
        } else if (authority.equals(user)) {
            successMessage = name + "(id : " + id + ")" + "님에게 일반 사용자 권한을 부여했습니다.";
        }
        return successMessage;
    }

    @Override
    public Page<RoleResponse> checkAuthority(Pageable pageable) {
        Page<Member> pagedMember = memberService.findAll(pageable);
        return pagedMember.map(member -> new RoleResponse(member.getId(), member.getRole(), member.getName()));
    }

    @Override
    public TokenResponse verifyUser(String accessToken, String refreshToken) {
        jwtTokenProvider.validateTokenResult(accessToken, refreshToken);
        String email = jwtTokenProvider.getEmailFromToken(accessToken);
        Member member = memberService.findByEmail(email);

        Long id = member.getId();
        RoleType role = memberService.findById(id).getRole();

        TokenResponse newTokenDto = new TokenResponse();
        newTokenDto.setRole(role);
        newTokenDto.setAccessToken(accessToken);
        newTokenDto.setRefreshToken(refreshToken);
        newTokenDto.setGrantType("Bearer");
        return newTokenDto;
    }
}