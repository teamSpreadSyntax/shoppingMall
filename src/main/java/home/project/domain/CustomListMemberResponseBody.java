package home.project.domain;

import java.util.List;

public class CustomListMemberResponseBody<T> {
    public List result;

    public String ResponsMessege;


    public CustomListMemberResponseBody(List result, String ResponsMessege) {
        this.result = result;
        this.ResponsMessege = ResponsMessege;
    }

}
