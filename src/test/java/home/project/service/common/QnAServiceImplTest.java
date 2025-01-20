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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
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
    private MemberService memberService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private QnAServiceImpl qnAService;

    @Mock
    private Converter converter;

    private Member testMember;
    private Product testProduct;
    private Orders testOrder;
    private QnA testQnA;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@test.com", null);
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
        testQnA.setAnswerStatus(AnswerStatus.ANSWERED);
        testQnA.setAnswerer(testMember);
    }

    @Nested
    @DisplayName("QnA 등록")
    class CreateQnATest {

        @Test
        @DisplayName("QnA 등록 성공")
        void shouldCreateQnASuccessfully() {
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
                            null,
                            "Subject",
                            "P12345",
                            "O12345",
                            "Description",
                            "test@test.com",
                            LocalDateTime.now(),
                            null,
                            null,
                            null,
                            AnswerStatus.WAITING
                    ));

            QnADetailResponse response = qnAService.join(requestDTO);

            assertThat(response).isNotNull();
            assertThat(response.getSubject()).isEqualTo("Subject");
            verify(qnARepository).save(any(QnA.class));
        }

        @Test
        @DisplayName("QnA 등록 실패: 인증 실패")
        void shouldFailToCreateQnAWithoutAuthentication() {
            SecurityContextHolder.getContext().setAuthentication(null);

            CreateQnARequestDTO requestDTO = new CreateQnARequestDTO();
            requestDTO.setProductNum("P12345");

            assertThatThrownBy(() -> qnAService.join(requestDTO))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("QnA 등록 실패: 잘못된 ProductNum")
        void shouldFailToCreateQnAWithInvalidProductNum() {
            CreateQnARequestDTO requestDTO = new CreateQnARequestDTO();
            requestDTO.setProductNum("INVALID");
            requestDTO.setOrderNum("O12345");

            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(productService.findByProductNum(anyString())).thenThrow(new IdNotFoundException("Product not found"));

            assertThatThrownBy(() -> qnAService.join(requestDTO))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("Product not found");
        }
    }

    @Nested
    @DisplayName("QnA 조회")
    class FindQnATest {

        @Test
        @DisplayName("QnA ID로 조회 성공")
        void shouldFindQnAByIdSuccessfully() {
            when(qnARepository.findById(anyLong())).thenReturn(Optional.of(testQnA));

            QnA result = qnAService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(qnARepository).findById(anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 QnA ID로 조회 실패")
        void shouldFailToFindQnAByNonexistentId() {
            when(qnARepository.findById(anyLong())).thenReturn(Optional.empty());

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
        void shouldDeleteQnAByIdSuccessfully() {
            qnAService.deleteById(1L);

            verify(qnARepository).deleteById(anyLong());
        }

        @Test
        @DisplayName("QnA 삭제 실패: 존재하지 않는 ID")
        void shouldFailToDeleteQnAByNonexistentId() {
            doThrow(new IdNotFoundException("QnA가 없습니다.")).when(qnARepository).deleteById(anyLong());

            assertThatThrownBy(() -> qnAService.deleteById(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("QnA가 없습니다.");
        }
    }

    @Nested
    @DisplayName("답변 추가")
    class AddAnswerTest {

        @Test
        @DisplayName("답변 추가 성공")
        void shouldAddAnswerSuccessfully() {
            testQnA.setAnswerStatus(AnswerStatus.WAITING); // WAITING 상태로 설정
            when(qnARepository.findById(anyLong())).thenReturn(Optional.of(testQnA));
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(converter.convertFromQnAToQnADetailResponse(any(QnA.class)))
                    .thenReturn(new QnADetailResponse(
                            1L,
                            null,
                            "Test Subject",
                            "P12345",
                            "O12345",
                            "Test Description",
                            "test@test.com",
                            LocalDateTime.now(),
                            "Test Answer",
                            LocalDateTime.now(),
                            "test@test.com",
                            AnswerStatus.ANSWERED
                    ));

            QnADetailResponse response = qnAService.addAnswer(1L, "Test Answer");

            assertThat(response).isNotNull();
            assertThat(response.getAnswer()).isEqualTo("Test Answer");
            verify(qnARepository).findById(anyLong());
        }


        @Test
        @DisplayName("답변 추가 실패: 답변 대기 상태가 아님")
        void shouldFailToAddAnswerIfNotWaiting() {
            testQnA.setAnswerStatus(AnswerStatus.ANSWERED);
            when(qnARepository.findById(anyLong())).thenReturn(Optional.of(testQnA));

            assertThatThrownBy(() -> qnAService.addAnswer(1L, "Test Answer"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("답변 대기중인 QnA만");
        }

        @Test
        @DisplayName("답변 추가 실패: 인증 실패")
        void shouldFailToAddAnswerWithoutAuthentication() {
            SecurityContextHolder.getContext().setAuthentication(null);

            assertThatThrownBy(() -> qnAService.addAnswer(1L, "Test Answer"))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    @DisplayName("답변 수정 성공")
    void shouldUpdateAnswerSuccessfully() {
        testQnA.setAnswerStatus(AnswerStatus.ANSWERED);
        testQnA.setAnswer("Old Answer");
        when(qnARepository.findById(anyLong())).thenReturn(Optional.of(testQnA));
        when(memberService.findByEmail(anyString())).thenReturn(testMember);
        when(converter.convertFromQnAToQnADetailResponse(any(QnA.class)))
                .thenReturn(new QnADetailResponse(
                        1L,                    // id
                        QnAType.ORDER,         // qnaType
                        "Test Subject",        // subject
                        "Test ProductNum",     // productNum
                        "Test OrderNum",       // orderNum
                        "Test Description",    // description
                        "test@test.com",       // memberEmail
                        LocalDateTime.now(),   // createAt
                        "Updated Answer",      // answer
                        LocalDateTime.now(),   // answerDate
                        "test@test.com",       // answererEmail
                        AnswerStatus.ANSWERED  // answerStatus
                ));

        QnADetailResponse response = qnAService.updateAnswer(1L, "Updated Answer");

        assertThat(response).isNotNull();
        assertThat(response.getAnswer()).isEqualTo("Updated Answer");
        verify(qnARepository).findById(anyLong());
    }




    @Test
        @DisplayName("답변 수정 실패: 상태가 답변 완료가 아님")
        void shouldFailToUpdateAnswerIfNotAnswered() {
            testQnA.setAnswerStatus(AnswerStatus.WAITING);
            when(qnARepository.findById(anyLong())).thenReturn(Optional.of(testQnA));

            assertThatThrownBy(() -> qnAService.updateAnswer(1L, "Updated Answer"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("답변 완료 상태의 QnA만 수정할 수 있습니다.");
        }

        @Test
        @DisplayName("답변 수정 실패: 인증 실패")
        void shouldFailToUpdateAnswerWithoutAuthentication() {
            SecurityContextHolder.getContext().setAuthentication(null);

            assertThatThrownBy(() -> qnAService.updateAnswer(1L, "Updated Answer"))
                    .isInstanceOf(NullPointerException.class);
        }
    }
