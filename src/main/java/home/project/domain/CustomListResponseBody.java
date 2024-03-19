package home.project.domain;

import java.util.List;

public class CustomListResponseBody<T> {
    public List listMember;

    public String ResponsMessege;


    public CustomListResponseBody(List listMember, String ResponsMessege) {
        this.listMember = listMember;
        this.ResponsMessege = ResponsMessege;
    }

}
