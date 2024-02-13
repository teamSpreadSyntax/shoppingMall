//package home.project;
//
//import home.project.repository.AdminRepository;
//import home.project.repository.MemberRepository;
//import home.project.service.AdminService;
//import home.project.service.AdminServiceImpl;
//import home.project.service.MemberService;
//import home.project.service.MemberServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//
//public class SpringConfig {
//
//    private final MemberRepository memberRepository;
//    private final AdminRepository adminRepository;
//
//    @Autowired
//    public SpringConfig(MemberRepository memberRepository, AdminRepository adminRepository) {
//        this.memberRepository = memberRepository;
//        this.adminRepository = adminRepository;
//    }
//
//    @Bean
//    public MemberService memberService(){
//        return new MemberServiceImpl(memberRepository);
//    }
//
//    @Bean
//    public AdminService adminService(){
//        return new AdminServiceImpl(adminRepository);
//    }
//
//}
