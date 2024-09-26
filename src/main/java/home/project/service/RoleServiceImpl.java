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
    public Optional<Role> findById(Long memberId) {
        Role role = roleRepository.findById(memberId).orElseThrow(() -> {
            throw new IdNotFoundException(memberId + "(으)로 등록된 회원이 없습니다.");
        });
        return Optional.ofNullable(role);
    }

    @Override
    @Transactional
    public Optional<Role> update(Role role) {

        Role existsMember = roleRepository.findById(role.getId())
                .orElseThrow(() -> new IdNotFoundException(role.getId() + "(으)로 등록된 회원이 없습니다."));
        Role existsRole = roleRepository.findById(existsMember.getId()).get();
        existsRole.setRole(role.getRole());
        roleRepository.save(existsRole);

        return Optional.of(existsRole);
    }

    @Override
    @Transactional
    public void join(Role role) {
        roleRepository.save(role);
    }
}
