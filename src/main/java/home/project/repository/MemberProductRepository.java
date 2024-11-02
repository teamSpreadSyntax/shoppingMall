package home.project.repository;

import home.project.domain.MemberProduct;
import home.project.domain.Product;
import home.project.domain.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProductRepository extends JpaRepository<MemberProduct, Long> {
    Page<MemberProduct> findAllByMemberId(Long id, Pageable pageable);

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}

