package home.project.domain;

import java.util.Optional;

public class CustomOptionalPageResponseBody<T> {
    public Optional result;

    public String ResponseMessage;

    public long total;

    public CustomOptionalPageResponseBody(Optional result, String ResponseMessage, long total) {
        this.result = result;
        this.ResponseMessage = ResponseMessage;
        this.total = total;
    }

}