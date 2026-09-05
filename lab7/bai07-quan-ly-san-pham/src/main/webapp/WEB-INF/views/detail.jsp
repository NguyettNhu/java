<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1>${sanPham.ten}</h1>
<p>Mã: ${sanPham.ma}</p>
<p>${sanPham.moTa}</p>
<p>Giá: ${sanPham.gia}</p>
<p>Tồn kho: ${sanPham.soLuong}</p>
<a href="${pageContext.request.contextPath}/san-pham">Quay lại</a>
