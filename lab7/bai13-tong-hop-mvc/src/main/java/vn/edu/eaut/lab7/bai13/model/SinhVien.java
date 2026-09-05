package vn.edu.eaut.lab7.bai13.model;

public class SinhVien {
  private String ma, ten, lop;

  public SinhVien() {}

  public SinhVien(String ma, String ten, String lop) {
    this.ma = ma;
    this.ten = ten;
    this.lop = lop;
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

  public String getLop() {
    return lop;
  }

  public void setLop(String v) {
    lop = v;
  }
}
