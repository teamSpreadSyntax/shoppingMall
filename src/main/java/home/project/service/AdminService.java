package home.project.service;

import home.project.domain.Member;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    Optional<Member> findById(Long memberId);

    List<Member> findAll();
}
