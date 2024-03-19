package home.project.domain;

import java.util.List;

public class CustomListResponseBody<T> {
    public List listMember;

    public String succesResponsMessege;


    public CustomListResponseBody(List listMember, String succesResponsMessege) {
        this.listMember = listMember;
        this.succesResponsMessege = succesResponsMessege;
    }

}
