package home.project.repository;

import home.project.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> findMembers(String name, String email, String phone, String role, String content, Pageable pageable);

}