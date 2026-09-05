package vn.edu.eaut.lab7.bai10.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.edu.eaut.lab7.bai10.repository.SanPhamRepository;

@WebServlet("/san-pham")
public class CatalogServlet extends HttpServlet {
  protected void doGet(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    r.setAttribute("danhSach", SanPhamRepository.findAll());
    r.getRequestDispatcher("/WEB-INF/views/catalog.jsp").forward(r, p);
  }
}
