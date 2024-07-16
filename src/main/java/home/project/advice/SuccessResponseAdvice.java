//package home.project.advice;
//
//import home.project.domain.CustomOptionalResponseEntity;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpResponse;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
//import jakarta.servlet.http.HttpServletResponse;
//import java.util.Optional;
//
//@RestControllerAdvice(basePackages = "home.project")
//public class SuccessResponseAdvice implements ResponseBodyAdvice<Object> {
//
//    @Override
//    public boolean supports(MethodParameter returnType, Class converterType) {
//        return true;
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object body, MethodParameter returnType,
//                                  MediaType selectedContentType, Class selectedConverterType,
//                                  ServerHttpRequest request, ServerHttpResponse response) {
//        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
//
//        int status = servletResponse.getStatus();
//        HttpStatus resolve = HttpStatus.resolve(status);
//
//        if (resolve == null) {
//            return body;
//        }
//
//        if (resolve.is2xxSuccessful()) {
//            if (body instanceof CustomOptionalResponseEntity) {
//                return body; // 이미 CustomOptionalResponseEntity 형식인 경우
//            } else {
//                return new CustomOptionalResponseEntity<>(Optional.ofNullable(body), "요청이 성공적으로 처리되었습니다.", resolve);
//            }
//        }
//
//        return body;
//    }
//}