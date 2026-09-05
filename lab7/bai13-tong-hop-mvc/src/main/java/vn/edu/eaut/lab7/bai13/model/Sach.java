package vn.edu.eaut.lab7.bai13.model;

public class Sach {
  private String ma, ten, tacGia;

  public Sach() {}

  public Sach(String ma, String ten, String tacGia) {
    this.ma = ma;
    this.ten = ten;
    this.tacGia = tacGia;
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

  public String getTacGia() {
    return tacGia;
  }

  public void setTacGia(String v) {
    tacGia = v;
  }
}
