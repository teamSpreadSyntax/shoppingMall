package home.project.repository.common;

import home.project.domain.common.AnswerStatus;
import home.project.domain.common.QnA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnARepository extends JpaRepository<QnA, Long>, QnARepositoryCustom  {
    Page<QnA> findAllByMemberId(Long memberId, Pageable pageable);

    Page<QnA> findByAnswerStatus(AnswerStatus answerStatus, Pageable pageable);

    Page<QnA> findAllByProductId(Long productId, Pageable pageable);

    @Query("SELECT q FROM QnA q " +
            "JOIN MemberProduct mp ON q.product.id = mp.product.id " +
            "WHERE mp.member.id = :sellerId")
    Page<QnA> findBySellerIdUsingMemberProduct(@Param("sellerId") Long sellerId, Pageable pageable);

}
