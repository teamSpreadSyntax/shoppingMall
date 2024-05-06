package home.project.domain;

import java.util.Optional;

public class CustomOptionalProductResponseBody<T> {
    public Optional result;

    public String ResponsMessege;

    public CustomOptionalProductResponseBody(Optional result, String ResponsMessege) {
        this.result = result;
        this.ResponsMessege = ResponsMessege;
    }

}
