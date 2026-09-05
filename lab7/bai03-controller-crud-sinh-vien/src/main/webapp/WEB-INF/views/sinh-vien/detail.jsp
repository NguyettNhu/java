<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1>Chi tiết sinh viên</h1>
<p>Mã: ${sinhVien.ma}</p>
<p>Họ tên: ${sinhVien.hoTen}</p>
<p>Lớp: ${sinhVien.lop}</p>
<p>Email: ${sinhVien.email}</p>
<a href="${pageContext.request.contextPath}/sinh-vien">Quay lại</a>
