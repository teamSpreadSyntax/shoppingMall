package home.project.service;

import home.project.domain.QnA;
import home.project.dto.requestDTO.CreateQnARequestDTO;
import home.project.dto.requestDTO.CreateReviewRequestDTO;
import home.project.dto.responseDTO.QnADetailResponse;
import home.project.dto.responseDTO.QnAResponse;
import home.project.dto.responseDTO.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse join(CreateReviewRequestDTO createReviewRequestDTO);

    /*QnA findById(Long qnAId);

    QnADetailResponse findByIdReturnQnADetailResponse(Long qnAId);

    Page<QnAResponse> findAll(Pageable pageable);

    Page<QnAResponse> findAllMyQnA(Pageable pageable);

    void deleteById(Long qnAid);*/
}
