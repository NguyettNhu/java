package vn.edu.eaut.lab6.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/students", "/student-form.jsp", "/welcome", "/welcome.jsp"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("AuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("username") != null;

        if (!loggedIn) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));
        if (!"ADMIN".equals(role) && isAdminOnlyRequest(req)) {
            req.getRequestDispatcher("/403.jsp").forward(req, resp);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("AuthFilter destroyed");
    }

    private boolean isAdminOnlyRequest(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if ("/student-form.jsp".equals(servletPath) || "/welcome".equals(servletPath)
                || "/welcome.jsp".equals(servletPath)) {
            return true;
        }

        if (!"/students".equals(servletPath)) {
            return false;
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String action = request.getParameter("action");
        return "form".equals(action) || "edit".equals(action);
    }
}
