package home.project.repository;

import home.project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository  extends JpaRepository<Member, Long>  {
}
