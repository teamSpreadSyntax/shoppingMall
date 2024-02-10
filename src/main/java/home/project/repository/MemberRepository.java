package home.project.repository;
import home.project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    public Optional<Member> findByName(Optional<String> name);
    //findByName 메서드는 회원의 이름을 기반으로 회원을 찾는 기능을 제공합니다. 따라서 해당 메서드는 이름에 해당하는 회원을 찾아서 반환해야 합니다. 회원의 이름만으로는 충분한 정보가 아니기 때문에 회원 객체(Member)를 반환하는 것이 더 적절합니다.
    //따라서 findByName 메서드의 반환 형식은 Optional<Member>가 되어야 합니다. 이렇게 하면 이름으로 검색된 회원을 옵셔널로 반환할 수 있으며, 해당 회원이 존재하지 않는 경우에는 빈 옵셔널을 반환할 수 있습니다. 따라서 클라이언트 코드에서는 옵셔널의 isPresent 메서드를 사용하여 회원의 존재 여부를 확인할 수 있습니다.
}
