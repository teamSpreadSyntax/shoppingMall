package home.project.repository;

import home.project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTableRepository extends JpaRepository<Member, String>{

}
