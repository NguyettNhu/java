package vn.edu.eaut.lab7.bai11.model;

public class SinhVien {
  private final String ma, hoTen, lop;

  public SinhVien(String ma, String hoTen, String lop) {
    this.ma = ma;
    this.hoTen = hoTen;
    this.lop = lop;
  }

  public String getMa() {
    return ma;
  }

  public String getHoTen() {
    return hoTen;
  }

  public String getLop() {
    return lop;
  }
}
