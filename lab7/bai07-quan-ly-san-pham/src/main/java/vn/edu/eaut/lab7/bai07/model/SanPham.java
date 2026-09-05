package vn.edu.eaut.lab7.bai07.model;

import java.math.BigDecimal;

public class SanPham {
  private String ma, ten, moTa;
  private BigDecimal gia;
  private int soLuong;

  public SanPham() {}

  public SanPham(String ma, String ten, String moTa, BigDecimal gia, int soLuong) {
    this.ma = ma;
    this.ten = ten;
    this.moTa = moTa;
    this.gia = gia;
    this.soLuong = soLuong;
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

  public String getMoTa() {
    return moTa;
  }

  public void setMoTa(String v) {
    moTa = v;
  }

  public BigDecimal getGia() {
    return gia;
  }

  public void setGia(BigDecimal v) {
    gia = v;
  }

  public int getSoLuong() {
    return soLuong;
  }

  public void setSoLuong(int v) {
    soLuong = v;
  }
}
