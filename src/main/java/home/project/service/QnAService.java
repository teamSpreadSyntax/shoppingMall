package home.project.service;

import home.project.domain.QnA;
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

    Page<QnAResponse> findAllMyQnA(Pageable pageable);

    void deleteById(Long qnAid);


}
