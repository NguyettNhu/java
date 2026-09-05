package vn.edu.eaut.lab7.bai11.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.edu.eaut.lab7.bai11.repository.SinhVienRepository;

@WebServlet("/sinh-vien")
public class SinhVienServlet extends HttpServlet {
  protected void doGet(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    int page = parse(r.getParameter("page"));
    int size = 5,
        total = SinhVienRepository.count(),
        pages = (int) Math.ceil((double) total / size);
    page = Math.max(1, Math.min(page, pages));
    r.setAttribute("danhSach", SinhVienRepository.findPage(page, size));
    r.setAttribute("page", page);
    r.setAttribute("totalPages", pages);
    r.getRequestDispatcher("/WEB-INF/views/list.jsp").forward(r, p);
  }

  private int parse(String s) {
    try {
      return Integer.parseInt(s);
    } catch (Exception e) {
      return 1;
    }
  }
}
