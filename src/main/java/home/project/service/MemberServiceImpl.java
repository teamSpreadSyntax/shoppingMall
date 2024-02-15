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
    }
    public void validateDuplicateMember (Member member) {
        memberRepository.findById(member.getId()).ifPresent(m -> { throw new IllegalStateException("이미 존재하는 회원입니다."); });
        memberRepository.findByPhone(Optional.ofNullable(member.getPhone())).ifPresent(m -> { throw new IllegalStateException("이미 존재하는 번호입니다."); });
    }
    public void joinConfirm (Member member){
        memberRepository.save(member);
    }

}
