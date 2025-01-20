package home.project.service.common;

import home.project.domain.common.QnA;
import home.project.dto.requestDTO.CreateQnARequestDTO;
import home.project.dto.responseDTO.QnADetailResponse;
import home.project.dto.responseDTO.QnAResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QnAService {
    QnADetailResponse join(CreateQnARequestDTO createQnARequestDTO);

    QnA findById(Long qnAId);

    QnADetailResponse findByIdReturnQnADetailResponse(Long qnAId);

    Page<QnAResponse> findAll(Pageable pageable);

    Page<QnAResponse> findProductQnA(Long productId, Pageable pageable);

    Page<QnADetailResponse> findAllForManager(Pageable pageable);

    Page<QnAResponse> findAllMyQnA(Pageable pageable);

    void deleteById(Long qnAid);

    QnADetailResponse addAnswer(Long qnAId, String answer);

    QnADetailResponse updateAnswer(Long qnAId, String answer);

    void deleteAnswer(Long qnAId);
}
