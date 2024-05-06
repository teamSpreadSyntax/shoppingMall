package home.project.domain;

import java.util.List;

public class CustomListProductResponseBody<T> {
    public List result;

    public String ResponsMessege;


    public CustomListProductResponseBody(List result, String ResponsMessege) {
        this.result = result;
        this.ResponsMessege = ResponsMessege;
    }

}
