package home.project.domain;

import java.util.Optional;

public class CustomOptionalResponseBody<T> {
    public Optional result;

    public String ResponseMessage;

    public CustomOptionalResponseBody(Optional result, String ResponseMessage) {
        this.result = result;
        this.ResponseMessage = ResponseMessage;
    }

}
