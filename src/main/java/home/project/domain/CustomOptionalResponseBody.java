package home.project.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.Optional;

public class CustomOptionalResponseBody<T> {
    @JsonSerialize(using = CustomOptionalSerializer.class)
    public Optional<T> result;
    public String responseMessage;
    public int status;

    public CustomOptionalResponseBody(Optional<T> result, String responseMessage, int status) {
        this.result = result;
        this.responseMessage = responseMessage;
        this.status = status;
    }

}
