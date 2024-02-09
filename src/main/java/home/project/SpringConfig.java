package home.project;

import home.project.repository.MemberRepository;
import home.project.service.MemberService;
import home.project.service.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class SpringConfig {

    private final MemberRepository memberRepository;
    @Autowired
    public SpringConfig(MemberRepository memberRepository ) {//인젝션 받으면 SpringDataJpa가 구현체를 만들어놓은것이 등록이 됨
        this.memberRepository = memberRepository;

    }


    @Bean
    public MemberService memberService(){
        return new MemberServiceImpl(/*memberRepository()*/memberRepository);
    }

}
