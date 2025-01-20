package home.project.util;

import home.project.response.CustomResponseBody;
import home.project.response.CustomResponseEntity;
import home.project.service.util.ValidationCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ValidationCheckTest {

    private ValidationCheck validationCheck;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validationCheck = new ValidationCheck();
    }

    @Test
    void validationChecks_WithErrors_ReturnsCustomResponseEntity() {
        // Arrange
        FieldError fieldError1 = new FieldError("objectName", "field1", "NotEmpty", false, null, null, "field1은 비어 있을 수 없습니다.");
        FieldError fieldError2 = new FieldError("objectName", "field2", "NotNull", false, null, null, "field2는 null이 아니어야 합니다.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // Act
        CustomResponseEntity<Map<String, String>> response = validationCheck.validationChecks(bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body is null!");
        assertEquals("입력값을 확인해주세요.", response.getBody().getResponseMessage());
        assertEquals(Optional.of(Map.of(
                "field1", "field1은 비어 있을 수 없습니다.",
                "field2", "field2는 null이 아니어야 합니다."
        )), response.getBody().getResult());
    }

    @Test
    void validationChecks_NoErrors_ReturnsNull() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        CustomResponseEntity<Map<String, String>> response = validationCheck.validationChecks(bindingResult);

        // Assert
        assertNull(response);
    }
}
