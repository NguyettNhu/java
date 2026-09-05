package vn.edu.eaut.lab7.bai09.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.edu.eaut.lab7.bai09.model.DiemSinhVien;
import vn.edu.eaut.lab7.bai09.repository.DiemRepository;

@WebServlet("/diem")
public class DiemServlet extends HttpServlet {
  private final DiemRepository repo = DiemRepository.getInstance();

  protected void doGet(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    String a = v(r.getParameter("action"));
    if (a.equals("new") || a.equals("edit")) {
      r.setAttribute(
          "diem", a.equals("new") ? new DiemSinhVien() : repo.findById(v(r.getParameter("ma"))));
      r.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(r, p);
      return;
    }
    r.setAttribute("danhSach", repo.findAll());
    r.getRequestDispatcher("/WEB-INF/views/list.jsp").forward(r, p);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    r.setCharacterEncoding("UTF-8");
    if ("delete".equals(r.getParameter("action"))) repo.delete(v(r.getParameter("maSinhVien")));
    else {
      try {
        DiemSinhVien d =
            new DiemSinhVien(
                v(r.getParameter("maSinhVien")),
                v(r.getParameter("hoTen")),
                num(r, "chuyenCan"),
                num(r, "giuaKy"),
                num(r, "cuoiKy"));
        if (d.getChuyenCan() < 0
            || d.getChuyenCan() > 10
            || d.getGiuaKy() < 0
            || d.getGiuaKy() > 10
            || d.getCuoiKy() < 0
            || d.getCuoiKy() > 10) throw new NumberFormatException();
        repo.save(d);
      } catch (NumberFormatException e) {
        r.setAttribute("error", "Điểm phải nằm trong khoảng 0 đến 10");
        r.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(r, p);
        return;
      }
    }
    p.sendRedirect(r.getContextPath() + "/diem");
  }

  private double num(HttpServletRequest r, String n) {
    return Double.parseDouble(v(r.getParameter(n)));
  }

  private String v(String s) {
    return s == null ? "" : s.trim();
  }
}
