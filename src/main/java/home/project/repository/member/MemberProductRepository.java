package home.project.repository.member;

import home.project.domain.member.MemberProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProductRepository extends JpaRepository<MemberProduct, Long> {
    Page<MemberProduct> findAllByMemberId(Long id, Pageable pageable);

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}

