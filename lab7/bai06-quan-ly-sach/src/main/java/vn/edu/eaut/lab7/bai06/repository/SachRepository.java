package vn.edu.eaut.lab7.bai06.repository;

import java.util.*;
import vn.edu.eaut.lab7.bai06.model.Sach;

public final class SachRepository {
  private static final SachRepository I = new SachRepository();
  private final List<Sach> d = new ArrayList<>();

  private SachRepository() {
    d.add(new Sach("S01", "Lập trình Java", "Nguyễn Văn A", "Giáo dục", 2024));
    d.add(new Sach("S02", "Cấu trúc dữ liệu", "Trần Văn B", "Khoa học", 2023));
  }

  public static SachRepository getInstance() {
    return I;
  }

  public synchronized List<Sach> findAll() {
    return new ArrayList<>(d);
  }

  public synchronized Sach findById(String ma) {
    return d.stream().filter(s -> s.getMa().equalsIgnoreCase(ma)).findFirst().orElse(null);
  }

  public synchronized List<Sach> search(String q) {
    String k = q == null ? "" : q.toLowerCase(Locale.ROOT);
    return d.stream()
        .filter(
            s ->
                s.getTen().toLowerCase(Locale.ROOT).contains(k)
                    || s.getTacGia().toLowerCase(Locale.ROOT).contains(k))
        .toList();
  }

  public synchronized void save(Sach s) {
    Sach o = findById(s.getMa());
    if (o == null) d.add(s);
    else {
      o.setTen(s.getTen());
      o.setTacGia(s.getTacGia());
      o.setNhaXuatBan(s.getNhaXuatBan());
      o.setNamXuatBan(s.getNamXuatBan());
    }
  }

  public synchronized void delete(String ma) {
    d.removeIf(s -> s.getMa().equalsIgnoreCase(ma));
  }
}
