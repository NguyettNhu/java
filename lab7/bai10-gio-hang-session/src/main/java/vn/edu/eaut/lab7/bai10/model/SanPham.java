package vn.edu.eaut.lab7.bai10.model;

import java.math.BigDecimal;

public class SanPham {
  private final String ma, ten;
  private final BigDecimal gia;

  public SanPham(String ma, String ten, BigDecimal gia) {
    this.ma = ma;
    this.ten = ten;
    this.gia = gia;
  }

  public String getMa() {
    return ma;
  }

  public String getTen() {
    return ten;
  }

  public BigDecimal getGia() {
    return gia;
  }
}
