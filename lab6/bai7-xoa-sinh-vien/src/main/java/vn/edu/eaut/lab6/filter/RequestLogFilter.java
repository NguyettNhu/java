package vn.edu.eaut.lab6.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebFilter("/*")
public class RequestLogFilter implements Filter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        Object username = session == null ? null : session.getAttribute("username");

        System.out.printf(
                "[ACCESS] time=%s method=%s uri=%s user=%s%n",
                LocalDateTime.now().format(FORMATTER),
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                username == null ? "guest" : username
        );

        chain.doFilter(request, response);
    }
}
