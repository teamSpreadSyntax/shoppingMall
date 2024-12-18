package home.project.service.common;

import home.project.domain.common.AnswerStatus;
import home.project.domain.common.QnA;
import home.project.domain.common.Review;
import home.project.domain.member.Member;
import home.project.domain.member.RoleType;
import home.project.domain.order.Orders;
import home.project.domain.product.Product;
import home.project.dto.requestDTO.CreateQnARequestDTO;
import home.project.dto.responseDTO.QnADetailResponse;
import home.project.dto.responseDTO.QnAResponse;
import home.project.dto.responseDTO.ReviewDetailResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.common.QnARepository;
import home.project.repository.order.OrderRepository;
import home.project.service.member.MemberService;
import home.project.service.order.OrderService;
import home.project.service.product.ProductService;
import home.project.service.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class QnAServiceImpl implements QnAService{
    private final QnARepository qnARepository;
    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final OrderService orderService;
    private final Converter converter;


    @Override
    @Transactional
    public QnADetailResponse join(CreateQnARequestDTO createQnARequestDTO){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Product product = null;
        Orders order = null;
        if(createQnARequestDTO.getProductNum() != null && !createQnARequestDTO.getProductNum().isEmpty()){
            product = productService.findByProductNum(createQnARequestDTO.getProductNum());
        }
        if (createQnARequestDTO.getOrderNum() != null && !createQnARequestDTO.getOrderNum().isEmpty()) {
            order = orderService.findByOrderNum(createQnARequestDTO.getOrderNum());
        }


        QnA qnA = new QnA();
        qnA.setQnAType(createQnARequestDTO.getQnAType());
        qnA.setMember(member);
        qnA.setSubject(createQnARequestDTO.getSubject());
        qnA.setProduct(product);
        qnA.setOrders(order);
        qnA.setCreateAt(LocalDateTime.now());
        qnA.setDescription(createQnARequestDTO.getDescription());
        qnA.setAnswerStatus(AnswerStatus.WAITING);

        qnARepository.save(qnA);

        return converter.convertFromQnAToQnADetailResponse(qnA);
    }

    @Override
    public QnA findById(Long qnAId) {
        return qnARepository.findById(qnAId)
                .orElseThrow(() -> new IdNotFoundException(qnAId + "(으)로 등록된 QnA가 없습니다."));
    }

    @Override
    public QnADetailResponse findByIdReturnQnADetailResponse(Long qnAId) {
        return converter.convertFromQnAToQnADetailResponse(findById(qnAId));
    }

    @Override
    public Page<QnAResponse> findAll(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Page<QnA> pagedQnA = qnARepository.findAll(pageable);

        if (member.getRole() == RoleType.center) {
        return converter.convertFromPagedQnAToPagedQnAResponse(pagedQnA);
        }
        else if (member.getRole() == RoleType.admin) {
            pagedQnA = qnARepository.findBySellerIdUsingMemberProduct(member.getId(), pageable);
        }

        return converter.convertFromPagedQnAToPagedQnAResponse(pagedQnA);
    }

    @Override
    public Page<QnAResponse> findProductQnA(Long productId, Pageable pageable) {
        return converter.convertFromPagedQnAToPagedQnAResponse(findAllByProductId(productId, pageable));
    }

    private Page<QnA> findAllByProductId(Long productId, Pageable pageable) {
        return qnARepository.findAllByProductId(productId, pageable);
    }

    @Override
    public Page<QnADetailResponse> findAllForManager(Pageable pageable){
        Page<QnA> pagedQnA = qnARepository.findAll(pageable);
        return converter.convertFromPagedQnAToPagedQnADetailResponse(pagedQnA);
    }

    @Override
    public Page<QnAResponse> findAllMyQnA(Pageable pageable){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        Page<QnA> pagedQnA = qnARepository.findAllByMemberId(member.getId(), pageable);

        return converter.convertFromPagedQnAToPagedQnAResponse(pagedQnA);
    }

    @Override
    @Transactional
    public void deleteById(Long qnAId) {
        qnARepository.deleteById(qnAId);
    }

    @Override
    @Transactional
    public QnADetailResponse addAnswer(Long qnAId, String answer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member answerer = memberService.findByEmail(email);

        QnA qnA = findById(qnAId);

        if (qnA.getAnswerStatus() != AnswerStatus.WAITING) {
            throw new IllegalStateException("답변 대기중인 QnA만 답변을 작성할 수 있습니다.");
        }

        qnA.setAnswer(answer);
        qnA.setAnswerDate(LocalDateTime.now());
        qnA.setAnswerer(answerer);
        qnA.setAnswerStatus(AnswerStatus.ANSWERED);

        return converter.convertFromQnAToQnADetailResponse(qnA);
    }

    @Override
    @Transactional
    public QnADetailResponse updateAnswer(Long qnAId, String answer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member answerer = memberService.findByEmail(email);

        QnA qnA = findById(qnAId);

        if (qnA.getAnswerStatus() != AnswerStatus.ANSWERED) {
            throw new IllegalStateException("답변 완료 상태의 QnA만 수정할 수 있습니다.");
        }

        if (!qnA.getAnswerer().getId().equals(answerer.getId())) {
            throw new IllegalStateException("답변 작성자만 수정할 수 있습니다.");
        }

        qnA.setAnswer(answer);
        qnA.setAnswerDate(LocalDateTime.now());

        return converter.convertFromQnAToQnADetailResponse(qnA);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long qnAId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member answerer = memberService.findByEmail(email);

        QnA qnA = findById(qnAId);

        if (qnA.getAnswerStatus() != AnswerStatus.ANSWERED) {
            throw new IllegalStateException("답변 완료 상태의 QnA만 삭제할 수 있습니다.");
        }

        if (!qnA.getAnswerer().getId().equals(answerer.getId())) {
            throw new IllegalStateException("답변 작성자만 삭제할 수 있습니다.");
        }

        qnA.setAnswer(null);
        qnA.setAnswerDate(null);
        qnA.setAnswerer(null);
        qnA.setAnswerStatus(AnswerStatus.DELETED);
    }

}
