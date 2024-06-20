package home.project.domain;


import java.util.Optional;

public class CustomOptionalResponseBody<T> {
    public Optional result;
    public String ResponseMessage;
    public int status;

    public CustomOptionalResponseBody(Optional result, String ResponseMessage, int status) {
        this.result = result;
        this.ResponseMessage = ResponseMessage;
        this.status = status;
    }

}
