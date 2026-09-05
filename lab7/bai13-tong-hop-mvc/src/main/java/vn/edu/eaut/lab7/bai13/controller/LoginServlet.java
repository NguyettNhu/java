package vn.edu.eaut.lab7.bai13.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  protected void doGet(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    r.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(r, p);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    r.setCharacterEncoding("UTF-8");
    if ("admin".equals(r.getParameter("username")) && "123456".equals(r.getParameter("password"))) {
      r.getSession().setAttribute("username", "admin");
      p.sendRedirect(r.getContextPath() + "/admin/index.jsp");
      return;
    }
    r.setAttribute("error", "Sai tài khoản hoặc mật khẩu");
    doGet(r, p);
  }
}
