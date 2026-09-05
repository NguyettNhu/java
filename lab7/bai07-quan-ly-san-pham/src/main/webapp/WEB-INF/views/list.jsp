<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h1>Quản lý sản phẩm</h1>
<a href="?action=new">Thêm sản phẩm</a>
<table border="1" cellpadding="7">
    <tr>
        <th>Mã</th>
        <th>Tên</th>
        <th>Mô tả</th>
        <th>Giá</th>
        <th>Số lượng</th>
        <th>Thao tác</th>
    </tr>
    <c:forEach var="s" items="${danhSach}">
        <tr>
            <td>${s.ma}</td>
            <td>${s.ten}</td>
            <td>${s.moTa}</td>
            <td>${s.gia}</td>
            <td>${s.soLuong}</td>
            <td>
                <a href="?action=detail&ma=${s.ma}">Xem</a> <a href="?action=edit&ma=${s.ma}">Sửa</a>
                <form method="post" style="display:inline">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="ma" value="${s.ma}">
                    <button>Xóa</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
