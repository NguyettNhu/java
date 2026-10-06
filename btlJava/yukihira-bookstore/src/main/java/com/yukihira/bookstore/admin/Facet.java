package com.yukihira.bookstore.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

/** Một lối tắt lọc kèm số lượng, ví dụ “Chờ xác nhận (5)”, đặt ngay trên bảng quản lý. */
public record Facet(String label, long count, String url, boolean active) {

    /** Tạo lối tắt giữ nguyên các bộ lọc đang dùng, chỉ đổi một tham số và quay về trang đầu. */
    public static Facet of(HttpServletRequest request, String name, String value, String label, long count) {
        UriComponentsBuilder link = UriComponentsBuilder.fromPath(request.getRequestURI()).query(request.getQueryString());
        link.replaceQueryParam("page");
        if (value == null) link.replaceQueryParam(name);
        else link.replaceQueryParam(name, value);
        String current = request.getParameter(name);
        boolean active = value == null ? current == null || current.isBlank() : value.equals(current);
        // Chuỗi truy vấn gốc đã được mã hóa sẵn nên chỉ ghép lại, không mã hóa lần nữa.
        return new Facet(label, count, link.build().toUriString(), active);
    }
}
