package home.project.service.validation;

import home.project.response.CustomResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationCheckTest {

    private ValidationCheck validationCheck;
    private BindingResult bindingResult;
    private Object target;

    @BeforeEach
    void setUp() {
        validationCheck = new ValidationCheck();
        target = new Object();
        bindingResult = new BeanPropertyBindingResult(target, "target");
    }

    @Nested
    @DisplayName("유효성 검사 테스트")
    class ValidationChecksTest {

        @Test
        @DisplayName("바인딩 에러가 없으면 null을 반환한다")
        void returnNullWhenNoErrors() {
            // when
            CustomResponseEntity<Map<String, String>> result = validationCheck.validationChecks(bindingResult);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("NotEmpty 에러에 대한 응답을 반환한다")
        void returnErrorMessageForNotEmptyValidation() {
            // given
            bindingResult.addError(new FieldError("target", "field1", null, false,
                    new String[]{"NotEmpty"}, null, "must not be empty"));

            // when
            CustomResponseEntity<Map<String, String>> result = validationCheck.validationChecks(bindingResult);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("NotBlank 에러에 대한 응답을 반환한다")
        void returnErrorMessageForNotBlankValidation() {
            // given
            bindingResult.addError(new FieldError("target", "field1", null, false,
                    new String[]{"NotBlank"}, null, "must not be blank"));

            // when
            CustomResponseEntity<Map<String, String>> result = validationCheck.validationChecks(bindingResult);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("NotNull 에러에 대한 응답을 반환한다")
        void returnErrorMessageForNotNullValidation() {
            // given
            bindingResult.addError(new FieldError("target", "field1", null, false,
                    new String[]{"NotNull"}, null, "must not be null"));

            // when
            CustomResponseEntity<Map<String, String>> result = validationCheck.validationChecks(bindingResult);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("여러 필드의 에러를 모두 포함한다")
        void returnErrorMessagesForMultipleFields() {
            // given
            bindingResult.addError(new FieldError("target", "field1", null, false,
                    new String[]{"NotEmpty"}, null, "must not be empty"));
            bindingResult.addError(new FieldError("target", "field2", null, false,
                    new String[]{"NotNull"}, null, "must not be null"));

            // when
            CustomResponseEntity<Map<String, String>> result = validationCheck.validationChecks(bindingResult);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("동일 필드의 첫 번째 에러만 포함한다")
        void returnFirstErrorMessageForSameField() {
            // given
            bindingResult.addError(new FieldError("target", "field1", null, false,
                    new String[]{"NotEmpty"}, null, "must not be empty"));
            bindingResult.addError(new FieldError("target", "field1", null, false,
                    new String[]{"Pattern"}, null, "invalid pattern"));

            // when
            CustomResponseEntity<Map<String, String>> result = validationCheck.validationChecks(bindingResult);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("다른 유효성 검사 에러도 처리한다")
        void returnOtherValidationErrorMessage() {
            // given
            bindingResult.addError(new FieldError("target", "field1", null, false,
                    new String[]{"Pattern"}, null, "invalid pattern"));

            // when
            CustomResponseEntity<Map<String, String>> result = validationCheck.validationChecks(bindingResult);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}