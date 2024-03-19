package home.project.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomOptionalResponseBody<T> {
    public Optional OptionalMember;

    public String succesResponsMessege;

    public CustomOptionalResponseBody(Optional OptionalMember, String succesResponsMessege) {
        this.OptionalMember = OptionalMember;
        this.succesResponsMessege = succesResponsMessege;
    }

}
