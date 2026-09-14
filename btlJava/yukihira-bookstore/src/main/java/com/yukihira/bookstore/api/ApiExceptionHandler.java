package com.yukihira.bookstore.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.yukihira.bookstore.api")
public class ApiExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    ProblemDetail notFound(HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, "Resource not found",
                "Không tìm thấy tài nguyên được yêu cầu.", request);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class})
    ProblemDetail badRequest(HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request",
                "Tham số hoặc dữ liệu gửi lên không hợp lệ.", request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail unreadableBody(HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid JSON",
                "Nội dung JSON không hợp lệ hoặc bị thiếu.", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    ProblemDetail authenticationFailed(HttpServletRequest request) {
        return problem(HttpStatus.UNAUTHORIZED, "Authentication failed",
                "Email hoặc mật khẩu không đúng, hoặc tài khoản không hoạt động.", request);
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
}
