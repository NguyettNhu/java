package vn.edu.eaut.lab7.bai13.repository;

import java.math.BigDecimal;
import java.util.*;
import vn.edu.eaut.lab7.bai13.model.*;

public final class DataRepository {
  private static final DataRepository INSTANCE = new DataRepository();
  private final List<SinhVien> sinhVien = new ArrayList<>();
  private final List<Sach> sach = new ArrayList<>();
  private final List<SanPham> sanPham = new ArrayList<>();

  private DataRepository() {
    sinhVien.add(new SinhVien("SV001", "Nguyễn Minh An", "CNTT1"));
    sinhVien.add(new SinhVien("SV002", "Trần Thu Hà", "CNTT2"));
    sach.add(new Sach("S01", "Lập trình Java", "Nguyễn Văn A"));
    sach.add(new Sach("S02", "Cấu trúc dữ liệu", "Trần Văn B"));
    sanPham.add(new SanPham("SP01", "Bàn phím", new BigDecimal("850000")));
    sanPham.add(new SanPham("SP02", "Chuột", new BigDecimal("350000")));
  }

  public static DataRepository getInstance() {
    return INSTANCE;
  }

  public synchronized List<SinhVien> findAllSinhVien() {
    return new ArrayList<>(sinhVien);
  }

  public synchronized SinhVien findSinhVien(String ma) {
    return sinhVien.stream().filter(x -> x.getMa().equalsIgnoreCase(ma)).findFirst().orElse(null);
  }

  public synchronized boolean save(SinhVien x) {
    SinhVien o = findSinhVien(x.getMa());
    if (o == null) return sinhVien.add(x);
    o.setTen(x.getTen());
    o.setLop(x.getLop());
    return true;
  }

  public synchronized void deleteSinhVien(String ma) {
    sinhVien.removeIf(x -> x.getMa().equalsIgnoreCase(ma));
  }

  public synchronized List<Sach> findAllSach() {
    return new ArrayList<>(sach);
  }

  public synchronized Sach findSach(String ma) {
    return sach.stream().filter(x -> x.getMa().equalsIgnoreCase(ma)).findFirst().orElse(null);
  }

  public synchronized boolean save(Sach x) {
    Sach o = findSach(x.getMa());
    if (o == null) return sach.add(x);
    o.setTen(x.getTen());
    o.setTacGia(x.getTacGia());
    return true;
  }

  public synchronized void deleteSach(String ma) {
    sach.removeIf(x -> x.getMa().equalsIgnoreCase(ma));
  }

  public synchronized List<SanPham> findAllSanPham() {
    return new ArrayList<>(sanPham);
  }

  public synchronized SanPham findSanPham(String ma) {
    return sanPham.stream().filter(x -> x.getMa().equalsIgnoreCase(ma)).findFirst().orElse(null);
  }

  public synchronized boolean save(SanPham x) {
    SanPham o = findSanPham(x.getMa());
    if (o == null) return sanPham.add(x);
    o.setTen(x.getTen());
    o.setGia(x.getGia());
    return true;
  }

  public synchronized void deleteSanPham(String ma) {
    sanPham.removeIf(x -> x.getMa().equalsIgnoreCase(ma));
  }
}
