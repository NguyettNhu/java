package vn.edu.eaut.lab7.bai05.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
  protected void doPost(HttpServletRequest r, HttpServletResponse p) throws IOException {
    r.getSession().invalidate();
    p.sendRedirect(r.getContextPath() + "/login");
  }
}
