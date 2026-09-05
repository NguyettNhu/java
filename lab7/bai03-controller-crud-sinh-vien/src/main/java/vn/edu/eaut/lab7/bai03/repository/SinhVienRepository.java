package vn.edu.eaut.lab7.bai03.repository;

import java.util.*;
import vn.edu.eaut.lab7.bai03.model.SinhVien;

public final class SinhVienRepository {
  private static final SinhVienRepository INSTANCE = new SinhVienRepository();
  private final List<SinhVien> data = new ArrayList<>();

  private SinhVienRepository() {
    data.add(new SinhVien("SV001", "Nguyễn Minh An", "CNTT1", "an@eaut.edu.vn"));
    data.add(new SinhVien("SV002", "Trần Thu Hà", "CNTT2", "ha@eaut.edu.vn"));
  }

  public static SinhVienRepository getInstance() {
    return INSTANCE;
  }

  public synchronized List<SinhVien> findAll() {
    return new ArrayList<>(data);
  }

  public synchronized SinhVien findById(String ma) {
    return data.stream().filter(s -> s.getMa().equalsIgnoreCase(ma)).findFirst().orElse(null);
  }

  public synchronized boolean add(SinhVien s) {
    if (findById(s.getMa()) != null) return false;
    return data.add(s);
  }

  public synchronized boolean update(SinhVien s) {
    SinhVien old = findById(s.getMa());
    if (old == null) return false;
    old.setHoTen(s.getHoTen());
    old.setLop(s.getLop());
    old.setEmail(s.getEmail());
    return true;
  }

  public synchronized void delete(String ma) {
    data.removeIf(s -> s.getMa().equalsIgnoreCase(ma));
  }
}
