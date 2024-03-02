package home.project.service;

import home.project.domain.Member;
import home.project.repository.AdminRepository;
import home.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    public Optional<Member> findById(Long memberId) {
        Member member = adminRepository.findById(memberId).orElseThrow(() -> { throw new IllegalStateException(memberId+"로 가입된 회원이 없습니다."); });
        return Optional.ofNullable(member);
    }

    public List<Member> findAll() {
        return adminRepository.findAll();
    }

}
