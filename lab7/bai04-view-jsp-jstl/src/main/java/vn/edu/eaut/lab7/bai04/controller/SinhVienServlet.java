package vn.edu.eaut.lab7.bai04.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.edu.eaut.lab7.bai04.model.SinhVien;
import vn.edu.eaut.lab7.bai04.repository.SinhVienRepository;

@WebServlet("/sinh-vien")
public class SinhVienServlet extends HttpServlet {
  private final SinhVienRepository repo = SinhVienRepository.getInstance();

  protected void doGet(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    String a = v(r.getParameter("action"));
    if (a.equals("new") || a.equals("edit")) {
      r.setAttribute(
          "sinhVien", a.equals("edit") ? repo.findById(v(r.getParameter("ma"))) : new SinhVien());
      r.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(r, p);
      return;
    }
    r.setAttribute("danhSach", repo.findAll());
    r.getRequestDispatcher("/WEB-INF/views/list.jsp").forward(r, p);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse p) throws IOException {
    r.setCharacterEncoding("UTF-8");
    if ("delete".equals(r.getParameter("action"))) repo.delete(v(r.getParameter("ma")));
    else
      repo.save(
          new SinhVien(
              v(r.getParameter("ma")),
              v(r.getParameter("hoTen")),
              v(r.getParameter("lop")),
              v(r.getParameter("email"))));
    p.sendRedirect(r.getContextPath() + "/sinh-vien");
  }

  private String v(String s) {
    return s == null ? "" : s.trim();
  }
}
