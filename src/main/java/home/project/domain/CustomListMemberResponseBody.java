package home.project.domain;

import java.util.List;

public class CustomListMemberResponseBody<T> {
    public List result;

    public String ResponseMessage;

    public CustomListMemberResponseBody(List result, String ResponseMessage) {
        this.result = result;
        this.ResponseMessage = ResponseMessage;
    }
}
