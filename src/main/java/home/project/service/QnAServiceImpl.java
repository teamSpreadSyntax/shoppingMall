package home.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.*;
import home.project.dto.requestDTO.CreateQnARequestDTO;
import home.project.dto.responseDTO.QnADetailResponse;
import home.project.dto.responseDTO.QnAResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
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
//    private final MemberOrderRepository memberOrderRepository;
//    private final ProductOrderRepository productOrderRepository;
    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CouponService couponService;
    private final ShippingRepository shippingRepository;
//    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
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
    public Page<QnAResponse> findAll(Pageable pageable){
        Page<QnA> pagedQnA = qnARepository.findAll(pageable);
        return converter.convertFromPagedQnAToPagedQnAResponse(pagedQnA);
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



}
