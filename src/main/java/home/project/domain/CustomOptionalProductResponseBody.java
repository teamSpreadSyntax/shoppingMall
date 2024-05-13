package home.project.domain;

import java.util.Optional;

public class CustomOptionalProductResponseBody<T> {
    public Optional result;

    public String ResponseMessage;

    public CustomOptionalProductResponseBody(Optional result, String ResponseMessage) {
        this.result = result;
        this.ResponseMessage = ResponseMessage;
    }

}
