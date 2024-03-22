package home.project.domain;

import java.util.Optional;

public class CustomOptionalProductResponseBody<T> {
    public Optional OptionalProduct;

    public String ResponsMessege;

    public CustomOptionalProductResponseBody(Optional OptionalProduct, String ResponsMessege) {
        this.OptionalProduct = OptionalProduct;
        this.ResponsMessege = ResponsMessege;
    }

}
