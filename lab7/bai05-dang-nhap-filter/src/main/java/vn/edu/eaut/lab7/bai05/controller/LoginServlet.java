package vn.edu.eaut.lab7.bai05.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  protected void doGet(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    r.getRequestDispatcher("/login.jsp").forward(r, p);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    r.setCharacterEncoding("UTF-8");
    String u = r.getParameter("username"), pw = r.getParameter("password");
    if ("admin".equals(u) && "123456".equals(pw)) {
      r.getSession().setAttribute("username", u);
      p.sendRedirect(r.getContextPath() + "/admin/index.jsp");
    } else {
      r.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
      r.getRequestDispatcher("/login.jsp").forward(r, p);
    }
  }
}
