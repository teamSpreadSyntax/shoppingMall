package home.project.service;

import home.project.domain.Role;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role findById(Long memberId) {
        return roleRepository.findById(memberId)
                .orElseThrow(() -> new IdNotFoundException(memberId + "(으)로 등록된 회원이 없습니다."));
    }

    @Override
    @Transactional
    public Role update(Role role) {

        Role existingRole = findById(role.getId());
        existingRole.setRole(role.getRole());
        return roleRepository.save(existingRole);
    }

    @Override
    @Transactional
    public void join(Role role) {
        roleRepository.save(role);
    }
}
