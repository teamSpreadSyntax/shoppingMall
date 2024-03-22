package home.project.domain;

import java.util.Optional;

public class CustomOptionalMemberResponseBody<T> {
    public Optional OptionalMember;

    public String ResponsMessege;

    public CustomOptionalMemberResponseBody(Optional OptionalMember, String ResponsMessege) {
        this.OptionalMember = OptionalMember;
        this.ResponsMessege = ResponsMessege;
    }

}
