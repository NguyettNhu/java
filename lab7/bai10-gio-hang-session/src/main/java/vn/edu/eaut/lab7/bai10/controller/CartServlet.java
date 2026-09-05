package vn.edu.eaut.lab7.bai10.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import vn.edu.eaut.lab7.bai10.model.*;
import vn.edu.eaut.lab7.bai10.repository.SanPhamRepository;

@WebServlet("/gio-hang")
public class CartServlet extends HttpServlet {
  protected void doGet(HttpServletRequest r, HttpServletResponse p)
      throws ServletException, IOException {
    Map<String, CartItem> cart = cart(r);
    BigDecimal total =
        cart.values().stream().map(CartItem::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add);
    r.setAttribute("cart", cart.values());
    r.setAttribute("tongTien", total);
    r.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(r, p);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse p) throws IOException {
    r.setCharacterEncoding("UTF-8");
    Map<String, CartItem> cart = cart(r);
    String ma = r.getParameter("ma"), action = r.getParameter("action");
    if ("add".equals(action)) {
      SanPham s = SanPhamRepository.findById(ma);
      if (s != null)
        cart.compute(
            ma,
            (k, item) ->
                item == null ? new CartItem(s, 1) : new CartItem(s, item.getSoLuong() + 1));
    } else if ("update".equals(action)) {
      int n = parse(r.getParameter("soLuong"));
      if (n <= 0) cart.remove(ma);
      else if (cart.containsKey(ma)) cart.get(ma).setSoLuong(n);
    } else if ("delete".equals(action)) cart.remove(ma);
    p.sendRedirect(r.getContextPath() + ("add".equals(action) ? "/san-pham" : "/gio-hang"));
  }

  @SuppressWarnings("unchecked")
  private Map<String, CartItem> cart(HttpServletRequest r) {
    HttpSession s = r.getSession();
    Map<String, CartItem> c = (Map<String, CartItem>) s.getAttribute("cart");
    if (c == null) {
      c = new LinkedHashMap<>();
      s.setAttribute("cart", c);
    }
    return c;
  }

  private int parse(String v) {
    try {
      return Integer.parseInt(v);
    } catch (Exception e) {
      return 0;
    }
  }
}
