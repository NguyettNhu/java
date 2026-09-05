package vn.edu.eaut.lab7.bai02.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import vn.edu.eaut.lab7.bai02.model.SinhVien;

public class SinhVienRepository {
  private final List<SinhVien> data = new ArrayList<>();

  public SinhVienRepository() {
    data.add(new SinhVien("SV001", "Nguyễn Minh An", "CNTT1", "an@eaut.edu.vn"));
    data.add(new SinhVien("SV002", "Trần Thu Hà", "CNTT2", "ha@eaut.edu.vn"));
  }

  public synchronized List<SinhVien> findAll() {
    return new ArrayList<>(data);
  }

  public synchronized SinhVien findById(String ma) {
    return data.stream().filter(s -> s.getMa().equalsIgnoreCase(ma)).findFirst().orElse(null);
  }

  public synchronized List<SinhVien> search(String keyword) {
    String key = keyword == null ? "" : keyword.toLowerCase(Locale.ROOT);
    return data.stream()
        .filter(
            s ->
                s.getMa().toLowerCase(Locale.ROOT).contains(key)
                    || s.getHoTen().toLowerCase(Locale.ROOT).contains(key))
        .toList();
  }

  public synchronized boolean add(SinhVien sinhVien) {
    if (findById(sinhVien.getMa()) != null) return false;
    return data.add(sinhVien);
  }

  public synchronized boolean update(SinhVien value) {
    SinhVien current = findById(value.getMa());
    if (current == null) return false;
    current.setHoTen(value.getHoTen());
    current.setLop(value.getLop());
    current.setEmail(value.getEmail());
    return true;
  }

  public synchronized boolean delete(String ma) {
    return data.removeIf(s -> s.getMa().equalsIgnoreCase(ma));
  }
}
