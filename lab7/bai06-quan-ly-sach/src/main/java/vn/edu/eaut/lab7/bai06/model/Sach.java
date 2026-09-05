package vn.edu.eaut.lab7.bai06.model;

public class Sach {
  private String ma, ten, tacGia, nhaXuatBan;
  private int namXuatBan;

  public Sach() {}

  public Sach(String ma, String ten, String tacGia, String nhaXuatBan, int nam) {
    this.ma = ma;
    this.ten = ten;
    this.tacGia = tacGia;
    this.nhaXuatBan = nhaXuatBan;
    this.namXuatBan = nam;
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

  public String getNhaXuatBan() {
    return nhaXuatBan;
  }

  public void setNhaXuatBan(String v) {
    nhaXuatBan = v;
  }

  public int getNamXuatBan() {
    return namXuatBan;
  }

  public void setNamXuatBan(int v) {
    namXuatBan = v;
  }
}
