package home.project.domain;

import java.util.List;

public class CustomListResponseBody<T> {
    public List result;

    public String ResponseMessage;


    public CustomListResponseBody(List result, String ResponseMessage) {
        this.result = result;
        this.ResponseMessage = ResponseMessage;
    }

}
