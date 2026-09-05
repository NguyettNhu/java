<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h1>Quản lý điểm sinh viên</h1>
<p>Tổng kết = chuyên cần 10% + giữa kỳ 30% + cuối kỳ 60%</p>
<a href="?action=new">Nhập điểm</a>
<table border="1" cellpadding="7">
    <tr>
        <th>Mã SV</th>
        <th>Họ tên</th>
        <th>Chuyên cần</th>
        <th>Giữa kỳ</th>
        <th>Cuối kỳ</th>
        <th>Tổng kết</th>
        <th>Xếp loại</th>
        <th>Thao tác</th>
    </tr>
    <c:forEach var="d" items="${danhSach}">
        <tr>
            <td>${d.maSinhVien}</td>
            <td>${d.hoTen}</td>
            <td>${d.chuyenCan}</td>
            <td>${d.giuaKy}</td>
            <td>${d.cuoiKy}</td>
            <td>${d.tongKet}</td>
            <td>
                <strong>${d.xepLoai}</strong>
            </td>
            <td>
                <a href="?action=edit&ma=${d.maSinhVien}">Sửa</a>
                <form method="post" style="display:inline">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="maSinhVien" value="${d.maSinhVien}">
                    <button>Xóa</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
