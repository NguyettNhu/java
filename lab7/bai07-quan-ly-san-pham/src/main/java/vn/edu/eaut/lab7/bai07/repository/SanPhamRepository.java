package vn.edu.eaut.lab7.bai07.repository;

import java.math.BigDecimal;
import java.util.*;
import vn.edu.eaut.lab7.bai07.model.SanPham;

public final class SanPhamRepository {
  private static final SanPhamRepository I = new SanPhamRepository();
  private final List<SanPham> d = new ArrayList<>();

  private SanPhamRepository() {
    d.add(new SanPham("SP01", "Bàn phím", "Bàn phím cơ", new BigDecimal("850000"), 12));
    d.add(new SanPham("SP02", "Chuột", "Chuột không dây", new BigDecimal("350000"), 20));
  }

  public static SanPhamRepository getInstance() {
    return I;
  }

  public synchronized List<SanPham> findAll() {
    return new ArrayList<>(d);
  }

  public synchronized SanPham findById(String ma) {
    return d.stream().filter(s -> s.getMa().equalsIgnoreCase(ma)).findFirst().orElse(null);
  }

  public synchronized void save(SanPham s) {
    SanPham o = findById(s.getMa());
    if (o == null) d.add(s);
    else {
      o.setTen(s.getTen());
      o.setMoTa(s.getMoTa());
      o.setGia(s.getGia());
      o.setSoLuong(s.getSoLuong());
    }
  }

  public synchronized void delete(String ma) {
    d.removeIf(s -> s.getMa().equalsIgnoreCase(ma));
  }
}
