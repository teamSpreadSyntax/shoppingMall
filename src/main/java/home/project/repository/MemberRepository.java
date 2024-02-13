package home.project.repository;
import home.project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    public Optional<Member> findByName(Optional<String> name);

}
//JPA 메서드 쿼리 생성 방식: Spring Data JPA는 메서드 이름과 반환 타입을 기반으로 자동으로 JPQL 쿼리를 생성합니다. findByName 메서드의 반환 타입이 Optional<String>인 경우 JPA는 String 값만 반환하는 쿼리를 생성하려 합니다. 하지만, MemberRepository는 Member 엔티티를 관리하는 Repository이므로 JPQL 쿼리는 Member 엔티티를 반환해야 합니다. 이 불일치로 인해 오류가 발생합니다.
//
//Spring Data JPA의 제약사항: 현재 Spring Data JPA의 기본 구현은 엔티티 객체 또는 Optional<엔티티 객체> 의 반환 타입만 지원하도록 설계되어 있습니다. Optional<String>과 같은 비엔티티 타입의 반환은 아직 지원하지 않습니다.
//
//엔티티 반환의 의미: findByName 메서드는 이름을 기준으로 Member 엔티티를 조회하는 역할을 합니다. 따라서 이 메서드는 조회된 엔티티의 존재 여부뿐만 아니라 엔티티 자체의 정보를 반환하는 것이 더 적합합니다. Optional<Member>를 사용하면 엔티티의 존재 여부와 함께 엔티티의 속성들을 모두 사용할 수 있습니다.
//
//        따라서, findByName 메서드의 반환 타입은 Optional<Member>로 유지하는 것이 올바릅니다...
