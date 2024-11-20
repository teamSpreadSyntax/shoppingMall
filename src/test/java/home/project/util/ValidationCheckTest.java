package home.project.util;

import home.project.repositoryForElasticsearch.MemberElasticsearchRepository;
import home.project.service.util.IndexToElasticsearch;
import home.project.service.util.ValidationCheck;
import home.project.response.CustomResponseBody;
import home.project.response.CustomResponseEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

    @Mock
    private IndexToElasticsearch indexToElasticsearch;

    @Mock
    private MemberElasticsearchRepository memberElasticsearchRepository;
    @Test
    void validationChecks_WithErrors_ShouldReturnBadRequest() {
        // Given: BindingResult has errors
        FieldError fieldError = new FieldError("name", "field", "default message");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        // When: validationChecks is called
        CustomResponseEntity<Map<String, String>> responseEntity = validationCheck.validationChecks(bindingResult);

        // Then: Verify the response entity
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        CustomResponseBody<Map<String, String>> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.getResult());
        assertEquals("입력값을 확인해주세요.", responseBody.getResponseMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.getStatus());

        // Verify the content of the map
        Optional<Map<String, String>> optionalResponseMap = (Optional<Map<String, String>>) responseBody.getResult();
        assertTrue(optionalResponseMap.isPresent());
        Map<String, String> responseMap = optionalResponseMap.get();
        assertEquals(1, responseMap.size());
        assertEquals("default message", responseMap.get("field"));
    }

    @Test
    void validationChecks_NoErrors_ShouldReturnNull() {
        // Given: BindingResult has no errors
        when(bindingResult.hasErrors()).thenReturn(false);

        // When: validationChecks is called
        CustomResponseEntity<Map<String, String>> responseEntity = validationCheck.validationChecks(bindingResult);

        // Then: Response entity should be null
        assertNull(responseEntity);
    }
}
