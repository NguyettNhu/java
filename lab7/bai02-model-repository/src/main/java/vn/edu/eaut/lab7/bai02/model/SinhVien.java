package vn.edu.eaut.lab7.bai02.model;

public class SinhVien {
  private String ma;
  private String hoTen;
  private String lop;
  private String email;

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

  public void setMa(String ma) {
    this.ma = ma;
  }

  public String getHoTen() {
    return hoTen;
  }

  public void setHoTen(String hoTen) {
    this.hoTen = hoTen;
  }

  public String getLop() {
    return lop;
  }

  public void setLop(String lop) {
    this.lop = lop;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
