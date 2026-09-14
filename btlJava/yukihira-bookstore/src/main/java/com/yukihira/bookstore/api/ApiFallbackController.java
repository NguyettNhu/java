package com.yukihira.bookstore.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiFallbackController {

    @RequestMapping(path = "/api/**", produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    ResponseEntity<ProblemDetail> notFound(HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "Không tìm thấy API được yêu cầu.");
        problem.setTitle("API endpoint not found");
        problem.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
}
