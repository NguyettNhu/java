<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h1>Quản lý lớp học</h1>
<form>
    <input name="q" value="${q}" placeholder="Mã hoặc tên lớp">
    <button>Tìm</button>
</form>
<a href="?action=new">Thêm lớp</a>
<table border="1" cellpadding="7">
    <tr>
        <th>Mã</th>
        <th>Tên lớp</th>
        <th>Cố vấn</th>
        <th>Sĩ số</th>
        <th>Thao tác</th>
    </tr>
    <c:forEach var="x" items="${danhSach}">
        <tr>
            <td>${x.ma}</td>
            <td>${x.ten}</td>
            <td>${x.coVan}</td>
            <td>${x.soLuongSinhVien}</td>
            <td>
                <a href="?action=detail&ma=${x.ma}">Xem</a> <a href="?action=edit&ma=${x.ma}">Sửa</a>
                <form method="post" style="display:inline">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="ma" value="${x.ma}">
                    <button>Xóa</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
