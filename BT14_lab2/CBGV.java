package BT14_lab2;

import java.util.Scanner;

public class CBGV extends Nguoi {
    private String maSoGV;
    private double luongCung;
    private double luongThuong;
    private double tienPhat;

    public CBGV() {}

    public CBGV(String hoTen, int tuoi, String queQuan, String maSoGV, double luongCung, double luongThuong, double tienPhat) {
        super(hoTen, tuoi, queQuan);
        this.maSoGV = maSoGV;
        this.luongCung = luongCung;
        this.luongThuong = luongThuong;
        this.tienPhat = tienPhat;
    }

    public String getMaSoGV() {
        return maSoGV;
    }

    public void setMaSoGV(String maSoGV) {
        this.maSoGV = maSoGV;
    }

    public double getLuongCung() {
        return luongCung;
    }

    public void setLuongCung(double luongCung) {
        this.luongCung = luongCung;
    }

    public double getLuongThuong() {
        return luongThuong;
    }

    public void setLuongThuong(double luongThuong) {
        this.luongThuong = luongThuong;
    }

    public double getTienPhat() {
        return tienPhat;
    }

    public void setTienPhat(double tienPhat) {
        this.tienPhat = tienPhat;
    }

    @Override
    public void nhapThongTin(Scanner scanner) {
        System.out.print("Nhap ma so GV: ");
        this.maSoGV = scanner.nextLine();
        super.nhapThongTin(scanner);
        System.out.print("Nhap luong cung: ");
        this.luongCung = Double.parseDouble(scanner.nextLine());
        System.out.print("Nhap luong thuong: ");
        this.luongThuong = Double.parseDouble(scanner.nextLine());
        System.out.print("Nhap tien phat: ");
        this.tienPhat = Double.parseDouble(scanner.nextLine());
    }

    public double tinhLuongThucLinh() {
        return luongCung + luongThuong - tienPhat;
    }

    @Override
    public void hienThiThongTin() {
        System.out.println("Ma so GV: " + maSoGV);
        super.hienThiThongTin();
        System.out.println("Luong cung: " + luongCung);
        System.out.println("Luong thuong: " + luongThuong);
        System.out.println("Tien phat: " + tienPhat);
        System.out.println("Luong thuc linh: " + tinhLuongThucLinh());
        System.out.println("---------------------------");
    }
}
