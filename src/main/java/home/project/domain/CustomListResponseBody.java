package home.project.domain;

import java.util.List;

public class CustomListResponseBody<T> {
    public List result;

    public String ResponseMessage;

    public long total;

    public CustomListResponseBody(List result, String ResponseMessage,long total) {
        this.result = result;
        this.ResponseMessage = ResponseMessage;
        this.total = total;
    }

}
