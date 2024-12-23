package home.project.exceptions;

import home.project.response.CustomResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

public class FileUploadExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<CustomResponseEntity<String>> handleMaxSizeException(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new CustomResponseEntity<>(
                        "파일 크기가 제한을 초과했습니다.",
                        "파일 업로드 실패",
                        HttpStatus.PAYLOAD_TOO_LARGE
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomResponseEntity<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(new CustomResponseEntity<>(
                        e.getMessage(),
                        "파일 업로드 실패",
                        HttpStatus.BAD_REQUEST
                ));
    }
}
