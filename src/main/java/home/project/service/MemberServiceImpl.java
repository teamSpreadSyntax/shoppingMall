package home.project.service;

import home.project.domain.LoginDto;
import home.project.domain.Member;
import home.project.domain.Product;
import home.project.domain.TokenDto;
import home.project.repository.MemberRepository;
//import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
//    private final  OAuth2ResourceServerProperties.Jwt jwt;
@Autowired
    public  MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder/*, AuthenticationManagerBuilder authenticationManagerBuilder, OAuth2ResourceServerProperties.Jwt jwt*/){
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
//        this.authenticationManagerBuilder = authenticationManagerBuilder;
//        this.jwt = jwt;
    }

//    public ResponseEntity<?> login(LoginDto loginDto){
////        Optional<Member> member = memberRepository.findByEmail(loginDto.getEmail());
////        if(member.orElse(null) == null || !passwordEncoder.matches(loginDto.getPassword(), member.get().getPassword())){
////            return response.fail("ID 또는 패스워드를 확인하세요" , HttpStatus.BAD_REQUEST);
////        }
////        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();
////        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
////        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
////        redisTemplate.opsForValue().set("RT:" + authentication.getName(), tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExpiresIn(), TimeUnit.MILLISECONDS);
////        if (member.get().getAuthority() == Authority.ROLE_USER) {
////            tokenDto.setInfo(member.get().getNickname());
////        } else {
////            tokenDto.setInfo(member.get().getStores().get(0).getName());
////        }
////        return response.success(tokenDto, "로그인에 성공했습니다.", HttpStatus.OK);
//    }

    public void join (Member member){
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
    }

    public Optional<Member> findByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> { throw new IllegalStateException(email+"로 가입된 회원이 없습니다."); });
        return Optional.ofNullable(member);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public void update (Member member){
        Member exsitsMember = memberRepository.findById(member.getId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));
        exsitsMember.setPassword(member.getPassword());
        exsitsMember.setName(member.getName());
        exsitsMember.setEmail(member.getEmail());
        exsitsMember.setPhone(member.getPhone());
        memberRepository.save(exsitsMember);
    }

    public void deleteMember(Member member){
        memberRepository.findById(member.getId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));
        memberRepository.deleteById(member.getId());
        System.out.println("삭제가 완료되었습니다");
    }

}
