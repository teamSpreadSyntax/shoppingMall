package home.project.service;

import home.project.domain.CustomOptionalResponseBody;
import home.project.domain.CustomOptionalResponseEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ValidationCheckTest {

    @Autowired
    private ValidationCheck validationCheck;

    @MockBean
    private BindingResult bindingResult;

    @Test
    void validationChecks_WithErrors_ShouldReturnBadRequest() {
        FieldError fieldError = new FieldError("name", "field", "default message");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        CustomOptionalResponseEntity<?> responseEntity = validationCheck.validationChecks(bindingResult);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        CustomOptionalResponseBody<?> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.result);
        assertEquals("입력값을 확인해주세요.", responseBody.responseMessage);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.status);


        Optional<?> optionalResponseMap = responseBody.result;

        Map<String, String> responseMap = (Map<String, String>) optionalResponseMap.get();
        assertEquals(1, responseMap.size());
        assertEquals("default message", responseMap.get("field"));
    }

    @Test
    void validationChecks_NoErrors_ShouldReturnNull() {

        when(bindingResult.hasErrors()).thenReturn(false);

        CustomOptionalResponseEntity<?> responseEntity = validationCheck.validationChecks(bindingResult);

        assertNull(responseEntity);
    }
}
