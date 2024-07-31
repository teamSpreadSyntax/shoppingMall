package home.project.response;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import home.project.util.CustomOptionalSerializer;

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
