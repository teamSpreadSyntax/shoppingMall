package home.project.service.common;

import home.project.domain.common.AnswerStatus;
import home.project.domain.common.QnA;
import home.project.domain.common.QnAType;
import home.project.domain.member.Member;
import home.project.domain.order.Orders;
import home.project.domain.product.Product;
import home.project.dto.requestDTO.CreateQnARequestDTO;
import home.project.dto.responseDTO.QnADetailResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.common.QnARepository;
import home.project.repository.order.OrderRepository;
import home.project.service.member.MemberService;
import home.project.service.order.OrderService;
import home.project.service.product.ProductService;
import home.project.service.util.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QnAServiceImplTest {

    @Mock
    private QnARepository qnARepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @Mock
    private Converter converter;

    @InjectMocks
    private QnAServiceImpl qnAService;

    private Member testMember;
    private Product testProduct;
    private Orders testOrder;
    private QnA testQnA;

    @BeforeEach
    void setUp() {
        // 테스트용 인증 객체 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@test.com", null);

        // SecurityContextHolder에 인증 객체 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@test.com");

        testProduct = new Product();
        testProduct.setId(1L);

        testOrder = new Orders();
        testOrder.setId(1L);

        testQnA = new QnA();
        testQnA.setId(1L);
        testQnA.setMember(testMember);
        testQnA.setProduct(testProduct);
        testQnA.setOrders(testOrder);
        testQnA.setAnswerStatus(AnswerStatus.WAITING);
    }

    @Nested
    @DisplayName("QnA 등록")
    class JoinTest {

        @Test
        @DisplayName("QnA 등록 성공")
        void joinSuccess() {
            // given
            CreateQnARequestDTO requestDTO = new CreateQnARequestDTO();
            requestDTO.setProductNum("P12345");
            requestDTO.setOrderNum("O12345");
            requestDTO.setSubject("Subject");
            requestDTO.setDescription("Description");

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findByProductNum(anyString())).thenReturn(testProduct);
            when(orderService.findByOrderNum(anyString())).thenReturn(testOrder);
            when(qnARepository.save(any(QnA.class))).thenReturn(testQnA);
            when(converter.convertFromQnAToQnADetailResponse(any(QnA.class)))
                    .thenReturn(new QnADetailResponse(
                            1L,
                            QnAType.ORDER,  // 적절한 QnAType 값
                            "subject",
                            "description",
                            "productNum",
                            "orderNum",
                            "memberName",
                            LocalDateTime.now(),
                            "answer",
                            LocalDateTime.now(),
                            "answerer",
                            AnswerStatus.WAITING
                    ));

            // when
            QnADetailResponse response = qnAService.join(requestDTO);

            // then
            assertThat(response).isNotNull();
            verify(qnARepository).save(any(QnA.class));
            verify(converter).convertFromQnAToQnADetailResponse(any(QnA.class));
        }
    }

    @Nested
    @DisplayName("QnA 조회")
    class FindByIdTest {

        @Test
        @DisplayName("QnA ID로 조회 성공")
        void findByIdSuccess() {
            // given
            when(qnARepository.findById(anyLong())).thenReturn(Optional.of(testQnA));

            // when
            QnA result = qnAService.findById(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(qnARepository).findById(anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 QnA ID로 조회 실패")
        void findByIdFail() {
            // given
            when(qnARepository.findById(anyLong())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> qnAService.findById(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("QnA가 없습니다.");
        }
    }

    @Nested
    @DisplayName("QnA 삭제")
    class DeleteQnATest {

        @Test
        @DisplayName("QnA ID로 삭제 성공")
        void deleteByIdSuccess() {
            // when
            qnAService.deleteById(1L);

            // then
            verify(qnARepository).deleteById(anyLong());
        }
    }

}
