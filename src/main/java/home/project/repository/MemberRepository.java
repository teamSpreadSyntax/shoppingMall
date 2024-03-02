package home.project.repository;
import home.project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByPassword(Optional<String> pw);
    Optional<Member> findByPhone(Optional<String> phone);///넣어줄때는 옵셔널로 하지 않아도 됨 그냥 스트링 네임이 맞음 값이 없을것 같으면 이 전에 서비스에서 확인해야함
    Optional<Member> findByName(Optional<String> name);
    Optional<Member> findByBirth(Optional<Date> birth);
}
