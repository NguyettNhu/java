package BT14_lab2;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private ArrayList<CBGV> danhSachCBGV;

    public Main() {
        danhSachCBGV = new ArrayList<>();
    }

    public void themCBGV(CBGV cbgv) {
        danhSachCBGV.add(cbgv);
        System.out.println("Da them can bo giang vien vao danh sach.");
    }

    public void xoaCBGV(String maSoGV) {
        CBGV gvCanXoa = null;
        for (CBGV gv : danhSachCBGV) {
            if (gv.getMaSoGV().equals(maSoGV)) {
                gvCanXoa = gv;
                break;
            }
        }
        
        if (gvCanXoa != null) {
            danhSachCBGV.remove(gvCanXoa);
            System.out.println("Da xoa can bo giang vien co ma " + maSoGV);
        } else {
            System.out.println("Khong tim thay can bo giang vien co ma " + maSoGV);
        }
    }

    public void hienThiDanhSach() {
        if (danhSachCBGV.isEmpty()) {
            System.out.println("Danh sach trong!");
            return;
        }
        System.out.println("--- Danh sach can bo giang vien ---");
        for (CBGV gv : danhSachCBGV) {
            gv.hienThiThongTin();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Main quanLy = new Main();
        
        while (true) {
            System.out.println("--- QUAN LY CAN BO GIANG VIEN ---");
            System.out.println("1. Them can bo giang vien");
            System.out.println("2. Xoa can bo giang vien theo ma so");
            System.out.println("3. Hien thi danh sach va tinh luong thuc linh");
            System.out.println("0. Thoat");
            System.out.print("Chon chuc nang: ");
            
            int chon;
            try {
                chon = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Vui long nhap so!");
                continue;
            }
            
            switch (chon) {
                case 1:
                    CBGV gvMoi = new CBGV();
                    try {
                        gvMoi.nhapThongTin(scanner);
                        quanLy.themCBGV(gvMoi);
                    } catch (Exception e) {
                        System.out.println("Thong tin nhap khong hop le, vui long thu lai!");
                    }
                    break;
                case 2:
                    System.out.print("Nhap ma so giang vien can xoa: ");
                    String maXoa = scanner.nextLine();
                    quanLy.xoaCBGV(maXoa);
                    break;
                case 3:
                    quanLy.hienThiDanhSach();
                    break;
                case 0:
                    System.out.println("Ket thuc chuong trinh.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Chon sai, vui long chon lai!");
            }
        }
    }
}
