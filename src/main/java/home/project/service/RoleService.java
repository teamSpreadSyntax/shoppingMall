package home.project.service;

import home.project.domain.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findById(Long memberId);

    Optional<Role> update(Role role);

    void join(Role role);
}
