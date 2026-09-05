package vn.edu.eaut.lab7.bai09.repository;

import java.util.*;
import vn.edu.eaut.lab7.bai09.model.DiemSinhVien;

public final class DiemRepository {
  private static final DiemRepository I = new DiemRepository();
  private final List<DiemSinhVien> d = new ArrayList<>();

  private DiemRepository() {
    d.add(new DiemSinhVien("SV001", "Nguyễn Minh An", 9, 8, 8.5));
    d.add(new DiemSinhVien("SV002", "Trần Thu Hà", 8, 7, 6.5));
  }

  public static DiemRepository getInstance() {
    return I;
  }

  public synchronized List<DiemSinhVien> findAll() {
    return new ArrayList<>(d);
  }

  public synchronized DiemSinhVien findById(String ma) {
    return d.stream().filter(x -> x.getMaSinhVien().equalsIgnoreCase(ma)).findFirst().orElse(null);
  }

  public synchronized void save(DiemSinhVien x) {
    DiemSinhVien o = findById(x.getMaSinhVien());
    if (o == null) d.add(x);
    else {
      o.setHoTen(x.getHoTen());
      o.setChuyenCan(x.getChuyenCan());
      o.setGiuaKy(x.getGiuaKy());
      o.setCuoiKy(x.getCuoiKy());
    }
  }

  public synchronized void delete(String ma) {
    d.removeIf(x -> x.getMaSinhVien().equalsIgnoreCase(ma));
  }
}
