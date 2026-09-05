package vn.edu.eaut.lab7.bai08.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.edu.eaut.lab7.bai08.model.LopHoc;
import vn.edu.eaut.lab7.bai08.repository.LopHocRepository;

@WebServlet("/lop-hoc")
public class LopHocServlet extends HttpServlet {
  private final LopHocRepository repo = LopHocRepository.getInstance();

  protected void doGet(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    String a = v(r.getParameter("action"));
    if (a.equals("new") || a.equals("edit") || a.equals("detail")) {
      r.setAttribute(
          "lopHoc", a.equals("new") ? new LopHoc() : repo.findById(v(r.getParameter("ma"))));
      r.getRequestDispatcher(
              a.equals("detail") ? "/WEB-INF/views/detail.jsp" : "/WEB-INF/views/form.jsp")
          .forward(r, p);
      return;
    }
    String q = v(r.getParameter("q"));
    r.setAttribute("q", q);
    r.setAttribute("danhSach", repo.search(q));
    r.getRequestDispatcher("/WEB-INF/views/list.jsp").forward(r, p);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    r.setCharacterEncoding("UTF-8");
    if ("delete".equals(r.getParameter("action"))) repo.delete(v(r.getParameter("ma")));
    else {
      try {
        int n = Integer.parseInt(v(r.getParameter("soLuongSinhVien")));
        if (n < 0) throw new NumberFormatException();
        repo.save(
            new LopHoc(
                v(r.getParameter("ma")), v(r.getParameter("ten")), v(r.getParameter("coVan")), n));
      } catch (NumberFormatException e) {
        r.setAttribute("error", "Số lượng sinh viên phải là số không âm");
        r.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(r, p);
        return;
      }
    }
    p.sendRedirect(r.getContextPath() + "/lop-hoc");
  }

  private String v(String s) {
    return s == null ? "" : s.trim();
  }
}
