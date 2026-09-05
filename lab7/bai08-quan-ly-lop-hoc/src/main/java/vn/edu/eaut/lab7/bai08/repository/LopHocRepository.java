package vn.edu.eaut.lab7.bai08.repository;

import java.util.*;
import vn.edu.eaut.lab7.bai08.model.LopHoc;

public final class LopHocRepository {
  private static final LopHocRepository I = new LopHocRepository();
  private final List<LopHoc> d = new ArrayList<>();

  private LopHocRepository() {
    d.add(new LopHoc("CNTT1", "Công nghệ thông tin 1", "ThS. Nguyễn An", 35));
    d.add(new LopHoc("QTKD2", "Quản trị kinh doanh 2", "ThS. Trần Hà", 42));
  }

  public static LopHocRepository getInstance() {
    return I;
  }

  public synchronized LopHoc findById(String ma) {
    return d.stream().filter(x -> x.getMa().equalsIgnoreCase(ma)).findFirst().orElse(null);
  }

  public synchronized List<LopHoc> search(String q) {
    String k = q == null ? "" : q.toLowerCase(Locale.ROOT);
    return d.stream()
        .filter(
            x ->
                x.getMa().toLowerCase(Locale.ROOT).contains(k)
                    || x.getTen().toLowerCase(Locale.ROOT).contains(k))
        .toList();
  }

  public synchronized void save(LopHoc x) {
    LopHoc o = findById(x.getMa());
    if (o == null) d.add(x);
    else {
      o.setTen(x.getTen());
      o.setCoVan(x.getCoVan());
      o.setSoLuongSinhVien(x.getSoLuongSinhVien());
    }
  }

  public synchronized void delete(String ma) {
    d.removeIf(x -> x.getMa().equalsIgnoreCase(ma));
  }
}
