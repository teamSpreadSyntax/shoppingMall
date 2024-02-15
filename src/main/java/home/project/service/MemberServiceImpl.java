package home.project.service;

import home.project.domain.Member;
import home.project.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
@Autowired
    public  MemberServiceImpl(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public void join (Member member){
        validateDuplicateMember(member);
        joinConfirm(member);
        emptyMember(member);
    }
    public void validateDuplicateMember (Member member) {
        memberRepository.findById(member.getId()).ifPresent(m -> { throw new IllegalStateException("이미 존재하는 회원(ID중복)입니다."); });
        memberRepository.findByPhone(Optional.ofNullable(member.getPhone())).ifPresent(m -> { throw new IllegalStateException("이미 존재하는 휴대폰 번호입니다."); });
    }
    public void emptyMember (Member member) {
        memberRepository.findById(member.getId()).orElseThrow(() -> { throw new IllegalStateException("ID를 입력해주세요."); });
        memberRepository.findByPhone(Optional.ofNullable(member.getPhone())).orElseThrow(() -> { throw new IllegalStateException("휴대폰 번호를 입력해주세요."); });
        memberRepository.findByName(Optional.ofNullable(member.getName())).orElseThrow(() -> { throw new IllegalStateException("이름을 입력해주세요."); });
        memberRepository.findByBirth(Optional.ofNullable(member.getBirth())).orElseThrow(() -> { throw new IllegalStateException("생일을 입력해주세요."); });
    }
    public void joinConfirm (Member member){
        memberRepository.save(member);
    }

}
