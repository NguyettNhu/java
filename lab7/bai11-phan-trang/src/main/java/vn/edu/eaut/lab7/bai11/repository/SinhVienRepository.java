package vn.edu.eaut.lab7.bai11.repository;

import java.util.*;
import vn.edu.eaut.lab7.bai11.model.SinhVien;

public final class SinhVienRepository {
  private static final List<SinhVien> D = new ArrayList<>();

  static {
    for (int i = 1; i <= 18; i++)
      D.add(new SinhVien(String.format("SV%03d", i), "Sinh viên " + i, "CNTT" + ((i - 1) % 3 + 1)));
  }

  private SinhVienRepository() {}

  public static List<SinhVien> findPage(int page, int size) {
    int from = Math.min((page - 1) * size, D.size()), to = Math.min(from + size, D.size());
    return new ArrayList<>(D.subList(from, to));
  }

  public static int count() {
    return D.size();
  }
}
