<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h1>CRUD sinh viên</h1>
<a href="?action=new">Thêm mới</a>
<table border="1" cellpadding="8">
    <tr>
        <th>Mã</th>
        <th>Họ tên</th>
        <th>Lớp</th>
        <th>Email</th>
        <th>Thao tác</th>
    </tr>
    <c:forEach var="s" items="${danhSach}">
        <tr>
            <td>${s.ma}</td>
            <td>${s.hoTen}</td>
            <td>${s.lop}</td>
            <td>${s.email}</td>
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
