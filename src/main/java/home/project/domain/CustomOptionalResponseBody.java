package home.project.domain;

import java.util.Optional;

public class CustomOptionalResponseBody<T> {
    public Optional OptionalMember;

    public String ResponsMessege;

    public CustomOptionalResponseBody(Optional OptionalMember, String ResponsMessege) {
        this.OptionalMember = OptionalMember;
        this.ResponsMessege = ResponsMessege;
    }

}
