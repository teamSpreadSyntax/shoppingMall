package home.project.service;

import home.project.domain.Role;

import java.util.Optional;

public interface RoleService {
    Role findById(Long memberId);

    Role update(Role role);

    void join(Role role);
}
