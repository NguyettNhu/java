package vn.edu.eaut.lab7.bai13.model;

import java.math.BigDecimal;

public class SanPham {
  private String ma, ten;
  private BigDecimal gia;

  public SanPham() {}

  public SanPham(String ma, String ten, BigDecimal gia) {
    this.ma = ma;
    this.ten = ten;
    this.gia = gia;
  }

  public String getMa() {
    return ma;
  }

  public void setMa(String v) {
    ma = v;
  }

  public String getTen() {
    return ten;
  }

  public void setTen(String v) {
    ten = v;
  }

  public BigDecimal getGia() {
    return gia;
  }

  public void setGia(BigDecimal v) {
    gia = v;
  }
}
