package vn.edu.eaut.lab7.bai13.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import vn.edu.eaut.lab7.bai13.model.*;
import vn.edu.eaut.lab7.bai13.repository.DataRepository;

@WebServlet({"/admin/sinh-vien", "/admin/sach", "/admin/san-pham"})
public class AdminCrudServlet extends HttpServlet {
  private final DataRepository repo = DataRepository.getInstance();

  protected void doGet(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    String module = module(r), action = v(r.getParameter("action"));
    r.setAttribute("module", module);
    r.setAttribute("moduleTitle", title(module));
    if (action.equals("new") || action.equals("edit")) {
      Object item = action.equals("new") ? empty(module) : find(module, v(r.getParameter("ma")));
      if (item == null) {
        p.sendError(404);
        return;
      }
      r.setAttribute("item", item);
      r.getRequestDispatcher("/WEB-INF/views/admin/form.jsp").forward(r, p);
      return;
    }
    r.setAttribute("items", findAll(module));
    r.getRequestDispatcher("/WEB-INF/views/admin/list.jsp").forward(r, p);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    r.setCharacterEncoding("UTF-8");
    String module = module(r), action = v(r.getParameter("action")), ma = v(r.getParameter("ma"));
    if (action.equals("delete")) {
      delete(module, ma);
      redirect(r, p, module);
      return;
    }
    String ten = v(r.getParameter("ten"));
    if (ma.isBlank() || ten.isBlank()) {
      formError(r, p, module, "Mã và tên không được để trống");
      return;
    }
    try {
      switch (module) {
        case "sinh-vien" -> repo.save(new SinhVien(ma, ten, v(r.getParameter("lop"))));
        case "sach" -> repo.save(new Sach(ma, ten, v(r.getParameter("tacGia"))));
        case "san-pham" -> {
          BigDecimal gia = new BigDecimal(v(r.getParameter("gia")));
          if (gia.signum() <= 0) throw new NumberFormatException();
          repo.save(new SanPham(ma, ten, gia));
        }
        default -> throw new IllegalArgumentException();
      }
    } catch (NumberFormatException e) {
      formError(r, p, module, "Giá sản phẩm phải lớn hơn 0");
      return;
    }
    redirect(r, p, module);
  }

  private Object empty(String m) {
    return switch (m) {
      case "sinh-vien" -> new SinhVien();
      case "sach" -> new Sach();
      case "san-pham" -> new SanPham();
      default -> null;
    };
  }

  private Object find(String m, String id) {
    return switch (m) {
      case "sinh-vien" -> repo.findSinhVien(id);
      case "sach" -> repo.findSach(id);
      case "san-pham" -> repo.findSanPham(id);
      default -> null;
    };
  }

  private Object findAll(String m) {
    return switch (m) {
      case "sinh-vien" -> repo.findAllSinhVien();
      case "sach" -> repo.findAllSach();
      case "san-pham" -> repo.findAllSanPham();
      default -> java.util.List.of();
    };
  }

  private void delete(String m, String id) {
    switch (m) {
      case "sinh-vien" -> repo.deleteSinhVien(id);
      case "sach" -> repo.deleteSach(id);
      case "san-pham" -> repo.deleteSanPham(id);
      default -> {}
    }
  }

  private void formError(HttpServletRequest r, HttpServletResponse p, String m, String error)
      throws ServletException, IOException {
    r.setAttribute("module", m);
    r.setAttribute("moduleTitle", title(m));
    r.setAttribute("item", empty(m));
    r.setAttribute("error", error);
    r.getRequestDispatcher("/WEB-INF/views/admin/form.jsp").forward(r, p);
  }

  private void redirect(HttpServletRequest r, HttpServletResponse p, String m) throws IOException {
    p.sendRedirect(r.getContextPath() + "/admin/" + m);
  }

  private String module(HttpServletRequest r) {
    return r.getServletPath().substring(r.getServletPath().lastIndexOf('/') + 1);
  }

  private String title(String m) {
    return switch (m) {
      case "sinh-vien" -> "Sinh viên";
      case "sach" -> "Sách";
      case "san-pham" -> "Sản phẩm";
      default -> "";
    };
  }

  private String v(String s) {
    return s == null ? "" : s.trim();
  }
}
