package vn.edu.eaut.lab7.bai10.repository;

import java.math.BigDecimal;
import java.util.*;
import vn.edu.eaut.lab7.bai10.model.SanPham;

public final class SanPhamRepository {
  private static final List<SanPham> D =
      List.of(
          new SanPham("SP01", "Bàn phím", new BigDecimal("850000")),
          new SanPham("SP02", "Chuột không dây", new BigDecimal("350000")),
          new SanPham("SP03", "Tai nghe", new BigDecimal("620000")));

  private SanPhamRepository() {}

  public static List<SanPham> findAll() {
    return D;
  }

  public static SanPham findById(String ma) {
    return D.stream().filter(s -> s.getMa().equals(ma)).findFirst().orElse(null);
  }
}
