package home.project.domain;


import java.util.Optional;

public class CustomOptionalResponseBody<T> {
    public Optional result;
    public String responseMessage;
    public int status;

    public CustomOptionalResponseBody(Optional result, String responseMessage, int status) {
        this.result = result;
        this.responseMessage = responseMessage;
        this.status = status;
    }

}
