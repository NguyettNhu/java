package vn.edu.eaut.lab7.bai04.repository;

import java.util.*;
import vn.edu.eaut.lab7.bai04.model.SinhVien;

public final class SinhVienRepository {
  private static final SinhVienRepository I = new SinhVienRepository();
  private final List<SinhVien> d = new ArrayList<>();

  private SinhVienRepository() {
    d.add(new SinhVien("SV001", "Nguyễn Minh An", "CNTT1", "an@eaut.edu.vn"));
    d.add(new SinhVien("SV002", "Trần Thu Hà", "CNTT2", "ha@eaut.edu.vn"));
  }

  public static SinhVienRepository getInstance() {
    return I;
  }

  public synchronized List<SinhVien> findAll() {
    return new ArrayList<>(d);
  }

  public synchronized SinhVien findById(String ma) {
    return d.stream().filter(s -> s.getMa().equalsIgnoreCase(ma)).findFirst().orElse(null);
  }

  public synchronized void save(SinhVien s) {
    SinhVien o = findById(s.getMa());
    if (o == null) d.add(s);
    else {
      o.setHoTen(s.getHoTen());
      o.setLop(s.getLop());
      o.setEmail(s.getEmail());
    }
  }

  public synchronized void delete(String ma) {
    d.removeIf(s -> s.getMa().equalsIgnoreCase(ma));
  }
}
