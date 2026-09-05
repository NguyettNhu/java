package vn.edu.eaut.lab7.bai08.model;

public class LopHoc {
  private String ma, ten, coVan;
  private int soLuongSinhVien;

  public LopHoc() {}

  public LopHoc(String ma, String ten, String coVan, int soLuong) {
    this.ma = ma;
    this.ten = ten;
    this.coVan = coVan;
    this.soLuongSinhVien = soLuong;
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

  public String getCoVan() {
    return coVan;
  }

  public void setCoVan(String v) {
    coVan = v;
  }

  public int getSoLuongSinhVien() {
    return soLuongSinhVien;
  }

  public void setSoLuongSinhVien(int v) {
    soLuongSinhVien = v;
  }
}
