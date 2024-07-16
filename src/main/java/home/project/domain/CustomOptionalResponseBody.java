package home.project.domain;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

@Schema(name = "CustomOptionalResponseBody", description = "Custom response body for optional data")
public class CustomOptionalResponseBody<T> {
    @Schema(description = "Response result", implementation = Object.class)
    public Optional<T> result;

    @Schema(description = "Response message")
    public String responseMessage;

    @Schema(description = "Response status code")
    public int status;

    public CustomOptionalResponseBody(Optional<T> result, String responseMessage, int status) {
        this.result = result;
        this.responseMessage = responseMessage;
        this.status = status;
    }

}
