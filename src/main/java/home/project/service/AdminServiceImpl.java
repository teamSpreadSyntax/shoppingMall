package home.project.service;

import home.project.domain.Member;
import home.project.repository.AdminRepository;
import home.project.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    public Optional<Member> findById(Long memberId) { return adminRepository.findById(memberId); }

    public List<Member> findAll() {
        return adminRepository.findAll();
    }

}
