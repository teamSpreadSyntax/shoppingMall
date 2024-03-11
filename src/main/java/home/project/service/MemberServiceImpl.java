package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;
import home.project.repository.MemberRepository;
//import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
@Autowired
    public  MemberServiceImpl(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public void login(Member member){}

    public void join (Member member){
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
