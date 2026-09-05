package vn.edu.eaut.lab7.bai03.model;

public class SinhVien {
  private String ma, hoTen, lop, email;

  public SinhVien() {}

  public SinhVien(String ma, String hoTen, String lop, String email) {
    this.ma = ma;
    this.hoTen = hoTen;
    this.lop = lop;
    this.email = email;
  }

  public String getMa() {
    return ma;
  }

  public void setMa(String v) {
    ma = v;
  }

  public String getHoTen() {
    return hoTen;
  }

  public void setHoTen(String v) {
    hoTen = v;
  }

  public String getLop() {
    return lop;
  }

  public void setLop(String v) {
    lop = v;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String v) {
    email = v;
  }
}
