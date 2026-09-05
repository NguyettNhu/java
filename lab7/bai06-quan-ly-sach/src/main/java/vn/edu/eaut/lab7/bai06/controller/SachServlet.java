package vn.edu.eaut.lab7.bai06.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.edu.eaut.lab7.bai06.model.Sach;
import vn.edu.eaut.lab7.bai06.repository.SachRepository;

@WebServlet("/sach")
public class SachServlet extends HttpServlet {
  private final SachRepository repo = SachRepository.getInstance();

  protected void doGet(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    String a = v(r.getParameter("action"));
    if (a.equals("new") || a.equals("edit") || a.equals("detail")) {
      Sach s = a.equals("new") ? new Sach() : repo.findById(v(r.getParameter("ma")));
      r.setAttribute("sach", s);
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
        repo.save(
            new Sach(
                v(r.getParameter("ma")),
                v(r.getParameter("ten")),
                v(r.getParameter("tacGia")),
                v(r.getParameter("nhaXuatBan")),
                Integer.parseInt(v(r.getParameter("namXuatBan")))));
      } catch (NumberFormatException e) {
        r.setAttribute("error", "Năm xuất bản không hợp lệ");
        r.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(r, p);
        return;
      }
    }
    p.sendRedirect(r.getContextPath() + "/sach");
  }

  private String v(String s) {
    return s == null ? "" : s.trim();
  }
}
