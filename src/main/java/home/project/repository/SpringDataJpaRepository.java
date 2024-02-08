package home.project.repository;

import home.project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaRepository extends JpaRepository<Member, Long> {

}
