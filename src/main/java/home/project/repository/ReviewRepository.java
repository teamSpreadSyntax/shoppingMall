package home.project.repository;

import home.project.domain.QnA;
import home.project.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByMemberId(Long memberId, Pageable pageable);
}
