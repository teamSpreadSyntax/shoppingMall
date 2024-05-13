package home.project.domain;

import java.util.List;

public class CustomListProductResponseBody<T> {
    public List result;

    public String ResponseMessage;


    public CustomListProductResponseBody(List result, String ResponseMessage) {
        this.result = result;
        this.ResponseMessage = ResponseMessage;
    }

}
