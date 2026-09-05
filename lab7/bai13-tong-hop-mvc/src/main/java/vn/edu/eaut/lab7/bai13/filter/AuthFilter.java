package vn.edu.eaut.lab7.bai13.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter("/admin/*")
public class AuthFilter implements Filter {
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest r = (HttpServletRequest) request;
    HttpServletResponse p = (HttpServletResponse) response;
    HttpSession s = r.getSession(false);
    if (s == null || s.getAttribute("username") == null) {
      p.sendRedirect(r.getContextPath() + "/login");
      return;
    }
    chain.doFilter(request, response);
  }
}
