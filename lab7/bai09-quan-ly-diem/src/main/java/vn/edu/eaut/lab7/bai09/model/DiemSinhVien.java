package vn.edu.eaut.lab7.bai09.model;

public class DiemSinhVien {
  private String maSinhVien, hoTen;
  private double chuyenCan, giuaKy, cuoiKy;

  public DiemSinhVien() {}

  public DiemSinhVien(String ma, String ten, double cc, double gk, double ck) {
    maSinhVien = ma;
    hoTen = ten;
    chuyenCan = cc;
    giuaKy = gk;
    cuoiKy = ck;
  }

  public String getMaSinhVien() {
    return maSinhVien;
  }

  public void setMaSinhVien(String v) {
    maSinhVien = v;
  }

  public String getHoTen() {
    return hoTen;
  }

  public void setHoTen(String v) {
    hoTen = v;
  }

  public double getChuyenCan() {
    return chuyenCan;
  }

  public void setChuyenCan(double v) {
    chuyenCan = v;
  }

  public double getGiuaKy() {
    return giuaKy;
  }

  public void setGiuaKy(double v) {
    giuaKy = v;
  }

  public double getCuoiKy() {
    return cuoiKy;
  }

  public void setCuoiKy(double v) {
    cuoiKy = v;
  }

  public double getTongKet() {
    return Math.round((chuyenCan * .1 + giuaKy * .3 + cuoiKy * .6) * 100.0) / 100.0;
  }

  public String getXepLoai() {
    double d = getTongKet();
    if (d >= 8.5) return "A";
    if (d >= 7) return "B";
    if (d >= 5.5) return "C";
    if (d >= 4) return "D";
    return "F";
  }
}
