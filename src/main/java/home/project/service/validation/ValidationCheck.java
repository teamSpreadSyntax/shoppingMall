package home.project.service.validation;

import home.project.response.CustomResponseBody;
import home.project.response.CustomResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ValidationCheck {
    public CustomResponseEntity<Map<String, String>> validationChecks(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> responseMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                String fieldName = error.getField();
                String errorMessage = error.getDefaultMessage();
                if ("NotEmpty".equals(error.getCode()) || "NotBlank".equals(error.getCode()) || "NotNull".equals(error.getCode())) {
                    responseMap.put(fieldName, errorMessage);
                } else if (!responseMap.containsKey(fieldName)) {
                    responseMap.put(fieldName, errorMessage);
                }
            }
            CustomResponseBody<Map<String, String>> errorBody = new CustomResponseBody<>(Optional.of(responseMap), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value());
            return new CustomResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
