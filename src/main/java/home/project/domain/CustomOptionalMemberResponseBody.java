package home.project.domain;

import java.util.Optional;

public class CustomOptionalMemberResponseBody<T> {
    public Optional result;

    public String ResponsMessege;

    public CustomOptionalMemberResponseBody(Optional result, String ResponsMessege) {
        this.result = result;
        this.ResponsMessege = ResponsMessege;
    }

}
