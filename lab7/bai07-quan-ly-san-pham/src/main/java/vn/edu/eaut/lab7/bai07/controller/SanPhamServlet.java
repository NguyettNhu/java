package vn.edu.eaut.lab7.bai07.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import vn.edu.eaut.lab7.bai07.model.SanPham;
import vn.edu.eaut.lab7.bai07.repository.SanPhamRepository;

@WebServlet("/san-pham")
public class SanPhamServlet extends HttpServlet {
  private final SanPhamRepository repo = SanPhamRepository.getInstance();

  protected void doGet(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    String a = v(r.getParameter("action"));
    if (a.equals("new") || a.equals("edit") || a.equals("detail")) {
      r.setAttribute(
          "sanPham", a.equals("new") ? new SanPham() : repo.findById(v(r.getParameter("ma"))));
      r.getRequestDispatcher(
              a.equals("detail") ? "/WEB-INF/views/detail.jsp" : "/WEB-INF/views/form.jsp")
          .forward(r, p);
      return;
    }
    r.setAttribute("danhSach", repo.findAll());
    r.getRequestDispatcher("/WEB-INF/views/list.jsp").forward(r, p);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    r.setCharacterEncoding("UTF-8");
    if ("delete".equals(r.getParameter("action"))) {
      repo.delete(v(r.getParameter("ma")));
      p.sendRedirect(r.getContextPath() + "/san-pham");
      return;
    }
    SanPham s;
    try {
      s =
          new SanPham(
              v(r.getParameter("ma")),
              v(r.getParameter("ten")),
              v(r.getParameter("moTa")),
              new BigDecimal(v(r.getParameter("gia"))),
              Integer.parseInt(v(r.getParameter("soLuong"))));
    } catch (NumberFormatException e) {
      error(r, p, null, "Giá hoặc số lượng không đúng định dạng");
      return;
    }
    if (s.getGia().signum() <= 0 || s.getSoLuong() < 0) {
      error(r, p, s, "Giá phải > 0 và số lượng phải >= 0");
      return;
    }
    repo.save(s);
    p.sendRedirect(r.getContextPath() + "/san-pham");
  }

  private void error(HttpServletRequest r, HttpServletResponse p, SanPham s, String m)
      throws ServletException, IOException {
    r.setAttribute("sanPham", s);
    r.setAttribute("error", m);
    r.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(r, p);
  }

  private String v(String s) {
    return s == null ? "" : s.trim();
  }
}
