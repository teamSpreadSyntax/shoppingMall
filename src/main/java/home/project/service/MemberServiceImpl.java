package home.project.service;

import home.project.domain.Member;
import home.project.repository.MemberRepository;
//import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;

import java.util.Optional;

import static org.apache.logging.log4j.ThreadContext.isEmpty;

@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
@Autowired
    public  MemberServiceImpl(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public void join (Member member){
        validateDuplicateMember(member);
//        emptyMember(member);
        joinConfirm(member);
    }
    public void validateDuplicateMember (Member member) {//@어노테이션으로 처리
        memberRepository.findById(member.getId()).ifPresent(m -> { throw new IllegalStateException("이미 존재하는 회원(ID중복)입니다."); });
        memberRepository.findByPhone(Optional.ofNullable(member.getPhone())).ifPresent(m -> { throw new IllegalStateException("이미 존재하는 휴대폰 번호입니다."); });
    }
//    public void emptyMember (Member member) {
//    System.out.println(member.getPassword());
////        if(member.getId()==null){ throw new IllegalStateException("ID를 입력해주세요."); }
////        if (StringUtils.isEmpty(member.getPassword())) {throw new IllegalStateException("비밀번호를 입력해주세요.");  }
//        if(member.getPassword()==null){ throw new IllegalStateException("비밀번호를 입력해주세요."); }
////        if(member.getPhone()==null){ throw new IllegalStateException("휴대폰 번호를 입력해주세요."); }
////        if(member.getName()==null){ throw new IllegalStateException("이름을 입력해주세요."); }
////        if(member.getBirth()==null){ throw new IllegalStateException("생일을 입력해주세요."); }
//////        memberRepository.findByPassword(Optional.ofNullable(member.getPassword())).orElseThrow(() -> { throw new IllegalStateException("비밀번호를 입력해주세요."); });
//////        memberRepository.findByPhone(Optional.ofNullable(member.getPhone())).orElseThrow(() -> { throw new IllegalStateException("휴대폰 번호를 입력해주세요."); });
//////        memberRepository.findByName(Optional.ofNullable(member.getName())).orElseThrow(() -> { throw new IllegalStateException("이름을 입력해주세요."); });
//////        memberRepository.findByBirth(Optional.ofNullable(member.getBirth())).orElseThrow(() -> { throw new IllegalStateException("생일을 입력해주세요."); });
//    }
    public void joinConfirm (Member member){
        memberRepository.save(member);
    }

    public void login(Member member){};

}
