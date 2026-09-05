package vn.edu.eaut.lab7.bai10.model;

import java.math.BigDecimal;

public class CartItem {
  private final SanPham sanPham;
  private int soLuong;

  public CartItem(SanPham s, int n) {
    sanPham = s;
    soLuong = n;
  }

  public SanPham getSanPham() {
    return sanPham;
  }

  public int getSoLuong() {
    return soLuong;
  }

  public void setSoLuong(int v) {
    soLuong = v;
  }

  public BigDecimal getThanhTien() {
    return sanPham.getGia().multiply(BigDecimal.valueOf(soLuong));
  }
}
