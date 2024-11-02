package home.project.repository;

import home.project.domain.AnswerStatus;
import home.project.domain.Cart;
import home.project.domain.QnA;
import home.project.dto.requestDTO.CreateQnARequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnARepository extends JpaRepository<QnA, Long>, QnARepositoryCustom  {
    Page<QnA> findAllByMemberId(Long memberId, Pageable pageable);

    Page<QnA> findByAnswerStatus(AnswerStatus answerStatus, Pageable pageable);

}
