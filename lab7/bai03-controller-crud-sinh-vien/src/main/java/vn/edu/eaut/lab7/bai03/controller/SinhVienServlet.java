package vn.edu.eaut.lab7.bai03.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.edu.eaut.lab7.bai03.model.SinhVien;
import vn.edu.eaut.lab7.bai03.repository.SinhVienRepository;

@WebServlet("/sinh-vien")
public class SinhVienServlet extends HttpServlet {
  private final SinhVienRepository repo = SinhVienRepository.getInstance();

  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String action = value(req.getParameter("action"));
    if (action.equals("new")) {
      req.setAttribute("sinhVien", new SinhVien());
      forward(req, resp, "form.jsp");
      return;
    }
    if (action.equals("edit") || action.equals("detail")) {
      SinhVien sv = repo.findById(value(req.getParameter("ma")));
      if (sv == null) {
        resp.sendError(404);
        return;
      }
      req.setAttribute("sinhVien", sv);
      forward(req, resp, action.equals("detail") ? "detail.jsp" : "form.jsp");
      return;
    }
    req.setAttribute("danhSach", repo.findAll());
    forward(req, resp, "list.jsp");
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String action = value(req.getParameter("action"));
    if (action.equals("delete")) {
      repo.delete(value(req.getParameter("ma")));
      redirect(req, resp);
      return;
    }
    SinhVien sv =
        new SinhVien(
            value(req.getParameter("ma")),
            value(req.getParameter("hoTen")),
            value(req.getParameter("lop")),
            value(req.getParameter("email")));
    if (sv.getMa().isBlank() || sv.getHoTen().isBlank()) {
      req.setAttribute("error", "Mã và họ tên không được trống");
      req.setAttribute("sinhVien", sv);
      forward(req, resp, "form.jsp");
      return;
    }
    if (action.equals("update")) repo.update(sv);
    else repo.add(sv);
    redirect(req, resp);
  }

  private void forward(HttpServletRequest r, HttpServletResponse p, String page)
      throws ServletException, IOException {
    r.getRequestDispatcher("/WEB-INF/views/sinh-vien/" + page).forward(r, p);
  }

  private void redirect(HttpServletRequest r, HttpServletResponse p) throws IOException {
    p.sendRedirect(r.getContextPath() + "/sinh-vien");
  }

  private String value(String s) {
    return s == null ? "" : s.trim();
  }
}
