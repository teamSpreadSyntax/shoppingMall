package home.project.service;

import home.project.domain.Member;
import home.project.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    public  MemberServiceImpl(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }
    public void join(Member member) {
        memberRepository.save(member);
    }

    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }
}
