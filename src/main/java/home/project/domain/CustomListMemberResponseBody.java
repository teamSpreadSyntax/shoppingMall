package home.project.domain;

import java.util.List;

public class CustomListMemberResponseBody<T> {
    public List listMember;

    public String ResponsMessege;


    public CustomListMemberResponseBody(List listMember, String ResponsMessege) {
        this.listMember = listMember;
        this.ResponsMessege = ResponsMessege;
    }

}
