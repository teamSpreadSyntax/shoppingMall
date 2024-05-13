package home.project.domain;

import java.util.Optional;

public class CustomOptionalMemberResponseBody<T> {
    public Optional result;

    public String ResponseMessage;

    public CustomOptionalMemberResponseBody(Optional result, String ResponseMessage) {
        this.result = result;
        this.ResponseMessage = ResponseMessage;
    }

}
