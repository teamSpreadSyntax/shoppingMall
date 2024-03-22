package home.project.domain;

import java.util.List;

public class CustomListProductResponseBody<T> {
    public List listProduct;

    public String ResponsMessege;


    public CustomListProductResponseBody(List listProduct, String ResponsMessege) {
        this.listProduct = listProduct;
        this.ResponsMessege = ResponsMessege;
    }

}
