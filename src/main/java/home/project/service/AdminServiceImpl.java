package home.project.service;

import home.project.domain.Member;
import home.project.repository.AdminRepository;
import home.project.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
@Autowired
    public AdminServiceImpl(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    public Optional<Member> findById(Long memberId) { return adminRepository.findById(memberId); }

    public List<Member> findAll() {
        return adminRepository.findAll();
    }

}
