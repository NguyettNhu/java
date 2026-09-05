<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h1>Danh sách sinh viên - 5 dòng/trang</h1>
<table border="1" cellpadding="8">
    <tr>
        <th>Mã</th>
        <th>Họ tên</th>
        <th>Lớp</th>
    </tr>
    <c:forEach var="s" items="${danhSach}">
        <tr>
            <td>${s.ma}</td>
            <td>${s.hoTen}</td>
            <td>${s.lop}</td>
        </tr>
    </c:forEach>
</table>
<p>
    <c:if test="${page>1}">
        <a href="?page=${page-1}">Trước</a>
    </c:if> <c:forEach begin="1" end="${totalPages}" var="i">
    <a href="?page=${i}">[${i}]</a> </c:forEach>
        <c:if test="${page<totalPages}">
            <a href="?page=${page+1}">Sau</a>
        </c:if>
    </p>
