package home.project.repository;
import home.project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByPhone(Optional<String> member);
    Optional<Object> findByName(Optional<String> name);
    Optional<Object> findByBirth(Optional<Date> birth);
}
