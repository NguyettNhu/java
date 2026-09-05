<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Sinh viên</title>
        <style>body{font-family:Segoe UI;margin:40px}table{border-collapse:collapse;width:100%}th,td{padding:10px;border:1px solid #ccd}th{background:#164e63;color:white}</style>
    </head>
    <body>
        <h1>Danh sách sinh viên</h1>
        <a href="?action=new">+ Thêm sinh viên</a>
        <table>
            <thead>
                <tr>
                    <th>Mã</th>
                    <th>Họ tên</th>
                    <th>Lớp</th>
                    <th>Email</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="sv" items="${danhSach}">
                    <tr>
                        <td>
                            <c:out value="${sv.ma}"/>
                        </td>
                        <td>
                            <c:out value="${sv.hoTen}"/>
                        </td>
                        <td>${sv.lop}</td>
                        <td>${sv.email}</td>
                        <td>
                            <a href="?action=edit&ma=${sv.ma}">Sửa</a>
                            <form method="post" style="display:inline">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="ma" value="${sv.ma}">
                                <button>Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty danhSach}">
                    <tr>
                        <td colspan="5">Chưa có dữ liệu</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </body>
</html>
