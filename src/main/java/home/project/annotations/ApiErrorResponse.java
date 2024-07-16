//package home.project.annotations;
//
//import home.project.domain.CustomOptionalResponseBody;
//import home.project.domain.CustomOptionalResponseEntity;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//
//@ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = CustomOptionalResponseEntity.class))),
//        @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = CustomOptionalResponseEntity.class))),
//        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = CustomOptionalResponseEntity.class))),
//        @ApiResponse(responseCode = "403", description = "Forbidden.", content = @Content(schema = @Schema(implementation = CustomOptionalResponseEntity.class))),
//        @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = CustomOptionalResponseEntity.class))),
//        @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema(implementation = CustomOptionalResponseEntity.class))),
//        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = CustomOptionalResponseEntity.class)))
//})
//@Retention(RetentionPolicy.RUNTIME)
//public @interface ApiErrorResponse {
//}
