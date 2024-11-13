package home.project.repository.common;

import home.project.domain.common.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByMemberId(Long memberId, Pageable pageable);
    Page<Review> findAllByProductId(Long productId, Pageable pageable);
}
