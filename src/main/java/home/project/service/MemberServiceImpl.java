package home.project.service;

import home.project.domain.Member;
import home.project.repository.MemberRepository;

import java.util.Optional;


public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    public  MemberServiceImpl(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public void join (Member member){
        validateDuplicateMember(Optional.ofNullable(member.getName()));
        joinConfirm(member);;
    }
    public void validateDuplicateMember (Optional < String > name) {
        memberRepository.findByName(name).ifPresent(m -> {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        });
    }
    public void joinConfirm (Member member){
        memberRepository.save(member);
    }

}
